package com.deliveryplatform.bookings;

import com.deliveryplatform.requests.events.RequestAcceptedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventHandler {

    private final BookingService bookingService;

    @EventListener
    public void onRequestAccepted(RequestAcceptedEvent event) {
        bookingService.create(event.requestId());
    }
}
