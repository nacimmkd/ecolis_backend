package com.deliveryplatform.payments;

import com.deliveryplatform.payments.dto.PaymentResponse;
import com.deliveryplatform.payments.dto.WebhookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/requests/{requestId}/checkout")
    public PaymentResponse createCheckoutSession(@PathVariable UUID requestId) {
        return paymentService.checkout(requestId);
    }

    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody String payload,
                               @RequestHeader("Stripe-Signature") String signature) {
        paymentService.handleWebHook(new WebhookRequest(signature, payload));
    }
}