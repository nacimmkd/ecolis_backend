package com.deliveryplatform.payments;

import com.deliveryplatform.payments.dto.PaymentResponse;
import com.deliveryplatform.users.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/requests/{requestId}/checkout")
    public PaymentResponse createCheckoutSession(@PathVariable UUID requestId) {
        return paymentService.authorize(requestId);
    }


    @GetMapping("/requests/{requestId}")
    public PaymentResponse getPaymentForRequest(@PathVariable UUID requestId) {
        return paymentService.getPaymentForRequest(requestId);
    }
}