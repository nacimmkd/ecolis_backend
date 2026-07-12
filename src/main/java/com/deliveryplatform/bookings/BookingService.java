package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;

import java.util.UUID;

public interface BookingService {

    BookingDto getBooking(UUID bookingId, UUID currentUserId);

    BookingDto create(UUID requestId);

    void cancel(UUID bookingId, String reason, UUID userId);

    void pay(UUID bookingId, UUID senderId);

    void confirmPickUp(UUID bookingId, String pickUpCode, UUID userId);

    void complete(UUID bookingId,String dropOfCode, UUID carrierId);
}