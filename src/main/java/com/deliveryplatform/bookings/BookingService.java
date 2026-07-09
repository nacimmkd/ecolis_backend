package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.bookings.dto.ParcelBookingDto;
import com.deliveryplatform.bookings.dto.TripBookingDto;
import com.deliveryplatform.requests.Request;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    BookingDto getBooking(UUID bookingId, UUID currentUserId);

    List<BookingDto> getMyBookings(UUID currentUserId);

    List<TripBookingDto> getTripBookings(UUID tripId, UUID currentUserId);

    List<ParcelBookingDto> getParcelBooking(UUID parcelId, UUID currentUserId);

    BookingDto create(Request request);

    void cancel(UUID bookingId, String reason, UUID userId);

    void pay(UUID bookingId, UUID senderId);

    void confirmPickUp(UUID bookingId, String pickUpCode, UUID userId);

    void complete(UUID bookingId,String dropOfCode, UUID carrierId);
}