package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    BookingDto getBooking(UUID bookingId, UUID currentUserId);

    List<BookingDto> getMyBookings(UUID currentUserId);

    List<BookingDto> getTripBookings(UUID tripId, UUID currentUserId);

    BookingDto getParcelBooking(UUID parcelId, UUID currentUserId);

    void cancel(UUID bookingId, String reason, UUID userId);

    void pay(UUID bookingId, UUID senderId);

    void complete(UUID bookingId, UUID carrierId);
}