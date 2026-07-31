package com.deliveryplatform.payments;

import com.deliveryplatform.auth.AuthService;
import com.deliveryplatform.payments.dto.*;
import com.deliveryplatform.payments.events.PaymentAuthorizedEvent;
import com.deliveryplatform.payments.exceptions.PaymentErrorCode;
import com.deliveryplatform.payments.exceptions.PaymentException;
import com.deliveryplatform.requests.Request;
import com.deliveryplatform.requests.RequestRepository;
import com.deliveryplatform.requests.exceptions.RequestErrorCode;
import com.deliveryplatform.requests.exceptions.RequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RequestRepository requestRepository;
    private final PaymentGateway paymentGateway;
    private final AuthService authService;
    private final PriceCalculator priceCalculator;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    @Transactional
    public PaymentResponse checkout(UUID requestId) {

        var request = getRequestByIdOrThrow(requestId);
        request.assertIsSender(authService.getCurrentUserPrincipal().getId());
        request.assertIsPending();

        Optional<Payment> existing = paymentRepository.findByRequestId(requestId);
        if (existing.isPresent()) {
            Payment payment = existing.get();
            if (payment.isPending()) {
                return PaymentResponse.from(payment,
                        paymentGateway.retrieveClientSecret(payment.getStripeCheckoutSessionId()));
            }
            throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_EXISTS, "Request already has a payment: " + requestId);
        }

        var price = priceCalculator.calculate(request);
        CheckoutSession session = paymentGateway.checkout(
                new CheckoutRequest(requestId, price.amount(), price.currency(), "Delivery request " + requestId)
        );

        Payment payment = paymentRepository.save(Payment.create(
                request,
                session.checkoutSessionId(),
                price.amount(),
                price.platformFees(),
                price.currency()));

        log.info("checkout opened requestId={} paymentId={} amount={}", requestId, payment.getId(), price.amount());

        return PaymentResponse.from(payment, session.clientSecret());
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

        if (payment.hasPaymentIntent()) {
            paymentGateway.cancel(payment.getStripePaymentIntentId());
        } else {
            paymentGateway.expireCheckoutSession(payment.getStripeCheckoutSessionId());
        }

        payment.markCanceled();
        return PaymentResponse.from(payment);
    }

    /** Capture manuelle : preleve reellement les fonds jusque-la bloques sur la carte du payeur. */
    @Override
    @Transactional
    public PaymentResponse capture(UUID requestId) {
        Payment payment = getRequestPayment(requestId);

        if (payment.isCaptured()) {
            return PaymentResponse.from(payment);
        }
        if (!payment.isAuthorized()) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_INVALID_STATE,
                    "Payment " + payment.getId() + " is not authorized, cannot capture");
        }

        String chargeId = paymentGateway.capture(payment.getStripePaymentIntentId(), null);
        payment.markSucceeded(chargeId);

        log.info("payment captured requestId={} paymentId={}", requestId, payment.getId());

        return PaymentResponse.from(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAuthorized(UUID requestId) {
        return paymentRepository.existsByRequestIdAndStatus(requestId, PaymentStatus.AUTHORIZED);
    }

    @Override
    @Transactional
    public void handleWebHook(WebhookRequest webhookRequest) {
        paymentGateway
                .parseWebhookRequest(webhookRequest)
                .ifPresent(this::applyWebhookEvent);
    }


    // private -----------------------------------------------------------------------------------------------

    private void applyWebhookEvent(WebhookResponse event) {
        Payment payment = resolvePayment(event);
        if (payment == null) {
            log.warn("webhook event ignored, no matching payment requestId={} paymentIntentId={}",
                    event.requestId(), event.paymentIntentId());
            return;
        }

        switch (event.status()) {
            case AUTHORIZED -> {
                boolean alreadyAuthorized = payment.isAuthorized();
                payment.markAuthorized(event.paymentIntentId());
                if (!alreadyAuthorized) {
                    eventPublisher.publishEvent(new PaymentAuthorizedEvent(event.requestId()));
                }
            }
            case FAILED -> payment.markFailed();
            case CANCELED -> payment.markCanceled();
            case SUCCEEDED -> {
                if (!payment.isCaptured()) {
                    payment.markSucceeded(null);
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
        return paymentRepository.findByRequestId(event.requestId()).orElse(null);
    }

    private Payment getRequestPayment(UUID requestId) {
        return paymentRepository.findByRequestId(requestId)
                .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND,
                        "No payment for request: " + requestId));
    }

    private Request getRequestByIdOrThrow(UUID requestId) {
        return requestRepository.findRequestById(requestId)
                .orElseThrow(() -> new RequestException(RequestErrorCode.REQUEST_NOT_FOUND, "Request not found: " + requestId));
    }

}
