package com.deliveryplatform.requests;

import com.deliveryplatform.payments.events.PaymentAuthorizedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestEventHandler {

    private final RequestService requestService;

    @EventListener
    public void onPaymentAuthorized(PaymentAuthorizedEvent event) {
        requestService.send(event.requestId());
    }
}
