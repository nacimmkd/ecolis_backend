package com.deliveryplatform.payments;

import com.deliveryplatform.payments.dto.PaymentResponse;
import com.deliveryplatform.payments.dto.WebhookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/bookings/{bookingId}")
    public PaymentResponse createCheckoutSession(@PathVariable UUID bookingId) {
        return paymentService.checkout(bookingId);
    }


    @PostMapping("/webhook/stripe")
    public void handleWebhook(@RequestBody String payload,
                              @RequestHeader Map<String, String> headers) {
        paymentService.handleWebHook(new WebhookRequest(headers, payload));
    }
}