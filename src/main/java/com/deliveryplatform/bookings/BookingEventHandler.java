package com.deliveryplatform.bookings;

import com.deliveryplatform.payments.events.PaymentAuthorizedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventHandler {

    private final BookingService bookingService;

    @EventListener
    public void onPaymentAuthorized(PaymentAuthorizedEvent event) {
        bookingService.requestDriver(event.bookingId(), event.payer().getId());
    }
}
