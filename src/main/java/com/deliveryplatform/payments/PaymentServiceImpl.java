package com.deliveryplatform.payments;

import com.deliveryplatform.auth.AuthService;
import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingRepository;
import com.deliveryplatform.payments.dto.*;
import com.deliveryplatform.payments.exceptions.PaymentErrorCode;
import com.deliveryplatform.payments.exceptions.PaymentException;
import com.deliveryplatform.requests.Request;
import com.deliveryplatform.requests.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final PaymentProvider paymentProvider;
    private final AuthService authService;
    private final PriceCalculator priceCalculator;


    @Override
    @Transactional
    public PaymentResponse authorize(UUID requestId) {

        var request = getRequestByIdOrThrow(requestId);
        request.assertIsSender( authService.getCurrentUserPrincipal().getId() );
        request.assertIsPending();

        Optional<Payment> existing = paymentRepository.findByRequestId(requestId);
        if (existing.isPresent()) {
            Payment payment = existing.get();
            if (payment.isPending()) {
                return PaymentResponse.from(payment,
                        paymentProvider.retrieveClientSecret(payment.getStripeCheckoutSessionId()));
            }
            throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_EXISTS, "Request already has a payment: " + requestId);
        }


        var price = priceCalculator.calculate(request);
        AuthorizeResponse response = paymentProvider.authorize(
                new AuthorizeRequest(requestId, price.amount(), price.currency(), buildLabel(request)));

        Payment payment = paymentRepository.save(Payment.create(
                request,
                response.checkoutSessionId(),
                price.amount(),
                price.platformFees(),
                price.currency()));

        log.info("checkout opened requestId={} paymentId={} amount={}", requestId, payment.getId(), price.amount());

        return PaymentResponse.from(payment, response.clientSecret());
    }

    @Override
    @Transactional
    public PaymentResponse capture(UUID requestId) {

        var request = getRequestByIdOrThrow(requestId);
        var payment = getRequestPayment(requestId);

        if (payment.isCaptured()) {
            return PaymentResponse.from(payment);
        }
        if (!payment.isAuthorized()) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_INVALID_STATE,
                    "Payment " + payment.getId() + " is " + payment.getStatus() + ", expected AUTHORIZED");
        }

        CaptureResponse response = paymentProvider.capture(payment.getStripePaymentIntentId(), null);
        payment.markSucceeded(response.chargeId());

        var booking = Booking.createFromRequest(request);
        bookingRepository.save(booking);

        log.info("funds captured requestId={} paymentId={} amount={}",
                requestId, payment.getId(), response.amountCaptured());

        return PaymentResponse.from(payment);
    }

    @Override
    @Transactional
    public PaymentResponse cancel(UUID requestId) {
        Payment payment = getRequestPayment(requestId);

        if (payment.getStatus() == PaymentStatus.CANCELED || payment.getStatus() == PaymentStatus.FAILED) {
            return PaymentResponse.from(payment);
        }
        if (payment.isCaptured()) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_INVALID_STATE,
                    "Payment " + payment.getId() + " already captured, a refund is required");
        }

        // avant paiement il n'y a pas d'intent : on ferme la session
        if (payment.hasPaymentIntent()) {
            paymentProvider.cancel(payment.getStripePaymentIntentId());
        } else {
            paymentProvider.expireCheckoutSession(payment.getStripeCheckoutSessionId());
        }

        payment.markCanceled();
        log.info("funds released requestId={} paymentId={}", requestId, payment.getId());

        return PaymentResponse.from(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAuthorized(UUID requestId) {
        return paymentRepository.existsByRequestIdAndStatus(requestId, PaymentStatus.AUTHORIZED);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentForRequest(UUID requestId) {
        Payment payment = getRequestPayment(requestId);
        payment.getRequest().assertInvolves(authService.getCurrentUserPrincipal().getId());
        return PaymentResponse.from(payment);
    }

    // ---- webhooks ------------------------------------------------------------

    @Override
    @Transactional
    public void handleCheckoutCompleted(String checkoutSessionId, String paymentIntentId) {
        onPayment(paymentRepository.findByStripeCheckoutSessionId(checkoutSessionId),
                "checkout.completed", checkoutSessionId,
                payment -> {
                    if (payment.isPending()) {
                        payment.markAuthorized(paymentIntentId);
                    }
                });
    }

    @Override
    @Transactional
    public void handleCheckoutExpired(String checkoutSessionId) {
        onPayment(paymentRepository.findByStripeCheckoutSessionId(checkoutSessionId),
                "checkout.expired", checkoutSessionId,
                payment -> {
                    if (payment.isPending()) {
                        payment.markCanceled();
                    }
                });
    }

    @Override
    @Transactional
    public void handleCaptured(String paymentIntentId, String chargeId) {
        onPayment(paymentRepository.findByStripePaymentIntentId(paymentIntentId),
                "intent.succeeded", paymentIntentId,
                payment -> {
                    if (!payment.isCaptured()) {
                        payment.markSucceeded(chargeId);
                    }
                });
    }

    @Override
    @Transactional
    public void handleFailed(String paymentIntentId) {
        onPayment(paymentRepository.findByStripePaymentIntentId(paymentIntentId),
                "intent.failed", paymentIntentId,
                payment -> {
                    if (payment.isPending()) {
                        payment.markFailed();
                    }
                });
    }

    @Override
    @Transactional
    public void handleCanceled(String paymentIntentId) {
        onPayment(paymentRepository.findByStripePaymentIntentId(paymentIntentId),
                "intent.canceled", paymentIntentId,
                payment -> {
                    if (payment.isPending() || payment.isAuthorized()) {
                        payment.markCanceled();
                    }
                });
    }

    /**
     * Stripe rejoue ses webhooks et ne garantit pas l'ordre : on ignore
     * silencieusement ce qui est deja traite, et on ne leve jamais d'erreur sur
     * une reference inconnue (l'event peut arriver avant notre commit).
     */
    private void onPayment(Optional<Payment> found, String event, String reference,
                           Consumer<Payment> action) {
        found.ifPresentOrElse(
                payment -> {
                    action.accept(payment);
                    log.info("webhook {} ref={} paymentId={} status={}",
                            event, reference, payment.getId(), payment.getStatus());
                },
                () -> log.warn("webhook {} for unknown ref={}", event, reference));
    }

    private Payment getRequestPayment(UUID requestId) {
        return paymentRepository.findByRequestId(requestId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND,
                        "No payment for request: " + requestId));
    }

    private String buildLabel(Request request) {
        return "Livraison colis " + request.getParcel().getId();
    }

    private Request getRequestByIdOrThrow(UUID requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.REQUEST_NOT_PAYABLE,
                        "Request not found: " + requestId));
    }

}