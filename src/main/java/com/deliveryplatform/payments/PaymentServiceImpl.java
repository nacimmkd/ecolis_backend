package com.deliveryplatform.payments;

import com.deliveryplatform.auth.AuthService;
import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingRepository;
import com.deliveryplatform.bookings.BookingState;
import com.deliveryplatform.bookings.exceptions.BookingErrorCode;
import com.deliveryplatform.bookings.exceptions.BookingException;
import com.deliveryplatform.payments.dto.*;
import com.deliveryplatform.payments.events.PaymentAuthorizedEvent;
import com.deliveryplatform.payments.exceptions.PaymentErrorCode;
import com.deliveryplatform.payments.exceptions.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentGateway paymentGateway;
    private final AuthService authService;
    private final PriceCalculator priceCalculator;
    private final ApplicationEventPublisher eventPublisher;

    // ---- public API -----------------------------------------------------------------------------------------

    @Override
    @Transactional
    public PaymentResponse checkout(UUID bookingId) {
        var booking = getBookingByIdOrThrow(bookingId);
        booking.assertIsSender(authService.getCurrentUserPrincipal().getId());
        booking.assertIsInState(BookingState.PENDING, "Booking is not pending");

        Optional<Payment> existing = paymentRepository.findByBookingId(bookingId);
        if (existing.isPresent()) {
            Payment payment = existing.get();
            if (payment.isPending()) {
                return PaymentResponse.from(payment,
                        paymentGateway.retrieveClientSecret(payment.getStripeCheckoutSessionId()));
            }
            throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_EXISTS, "Booking already has a payment: " + bookingId);
        }

        var quote = priceCalculator.calculate(booking);
        CheckoutSession session = paymentGateway.checkout(
                new CheckoutRequest(bookingId, quote.total(), "Delivery booking " + bookingId)
        );

        Payment payment = paymentRepository.save(Payment.create(
                booking,
                session.checkoutSessionId(),
                quote.total(),
                quote.applicationFeeInCents()));

        log.info("checkout opened bookingId={} paymentId={} amount={}", bookingId, payment.getId(), quote.total().getAmountInCents());

        return PaymentResponse.from(payment, session.clientSecret());
    }

    @Override
    @Transactional
    public void cancel(UUID bookingId) {
        paymentRepository.findByBookingId(bookingId)
                .ifPresent(payment -> {
                    if (payment.isCaptured()) {
                        doRefund(payment);
                    } else {
                        doCancel(payment);
                    }
                });
    }

    @Override
    @Transactional
    public void cancelAll(List<UUID> bookingIds) {
        if (bookingIds.isEmpty()) {
            return;
        }

        paymentRepository.findByBookingIdIn(bookingIds).forEach(payment -> {
            if (payment.isCaptured()) {
                doRefund(payment);
            } else {
                doCancel(payment);
            }
        });
    }

    /** Capture manuelle : preleve reellement les fonds jusque-la bloques sur la carte du payeur. */
    @Override
    @Transactional
    public PaymentResponse capture(UUID bookingId) {
        Payment payment = getBookingPayment(bookingId);

        if (payment.isCaptured()) {
            return PaymentResponse.from(payment);
        }
        if (!payment.isAuthorized()) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_INVALID_STATE,
                    "Payment " + payment.getId() + " is not authorized, cannot capture");
        }

        String chargeId = paymentGateway.capture(payment.getStripePaymentIntentId(), null);
        payment.markSucceeded(chargeId);

        log.info("payment captured bookingId={} paymentId={}", bookingId, payment.getId());

        return PaymentResponse.from(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAuthorized(UUID bookingId) {
        return paymentRepository.existsByBookingIdAndStatus(bookingId, PaymentStatus.AUTHORIZED);
    }

    @Override
    @Transactional
    public void handleWebHook(WebhookRequest webhookRequest) {
        paymentGateway
                .parseWebhookRequest(webhookRequest)
                .ifPresent(this::applyWebhookEvent);
    }

    // ---- private ----------------------------------------------------------------------------------------------

    private void doCancel(Payment payment) {
        if (payment.getStatus() == PaymentStatus.CANCELED || payment.getStatus() == PaymentStatus.FAILED) {
            return;
        }

        if (payment.hasPaymentIntent()) {
            paymentGateway.cancel(payment.getStripePaymentIntentId());
        } else {
            paymentGateway.expireCheckoutSession(payment.getStripeCheckoutSessionId());
        }

        payment.markCanceled();
    }

    private void doRefund(Payment payment) {
        if (payment.isRefunded()) {
            return;
        }
        if (payment.getStripePaymentIntentId() == null) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_INVALID_STATE,
                    "Payment " + payment.getId() + " has no payment intent recorded, cannot refund");
        }

        String refundId = paymentGateway.refund(payment.getStripePaymentIntentId(), payment.getPrice().getAmountInCents());
        payment.markRefunded(refundId);

        log.info("payment refunded bookingId={} paymentId={}", payment.getBooking().getId(), payment.getId());
    }

    private void applyWebhookEvent(WebhookResponse event) {
        Payment payment = resolvePayment(event);
        if (payment == null) {
            log.warn("webhook event ignored, no matching payment bookingId={} paymentIntentId={}",
                    event.bookingId(), event.paymentIntentId());
            return;
        }

        switch (event.status()) {
            case AUTHORIZED -> {
                boolean alreadyAuthorized = payment.isAuthorized();
                payment.markAuthorized(event.paymentIntentId());
                if (!alreadyAuthorized) {
                    eventPublisher.publishEvent(new PaymentAuthorizedEvent(payment.getBooking().getId(), payment.getPayer()));
                }
            }
            case FAILED -> payment.markFailed();
            case CANCELED -> payment.markCanceled();
            case SUCCEEDED -> {
                if (!payment.isCaptured()) {
                    payment.markSucceeded(event.paymentIntentId());
                }
            }
            default -> log.debug("unhandled webhook status={} paymentId={}", event.status(), payment.getId());
        }

        paymentRepository.save(payment);
    }

    private Payment resolvePayment(WebhookResponse event) {
        if (event.paymentIntentId() != null) {
            Optional<Payment> byIntent = paymentRepository.findByStripePaymentIntentId(event.paymentIntentId());
            if (byIntent.isPresent()) {
                return byIntent.get();
            }
        }
        return paymentRepository.findByBookingId(event.bookingId()).orElse(null);
    }

    private Payment getBookingPayment(UUID bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND,
                        "No payment for booking: " + bookingId));
    }

    private Booking getBookingByIdOrThrow(UUID bookingId) {
        return bookingRepository.findBookingById(bookingId)
                .orElseThrow(() -> new BookingException(BookingErrorCode.BOOKING_NOT_FOUND, "Booking not found: " + bookingId));
    }

}