package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingCreateRequest;
import com.deliveryplatform.bookings.dto.BookingRequestDto;
import com.deliveryplatform.bookings.dto.BookingDto;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    BookingDto getBooking(UUID bookingId, UUID currentUserId);

    List<BookingDto> getMyBookings(UUID currentUserId);

    List<BookingDto> getTripBookings(UUID tripId, UUID currentUserId);

    BookingDto getParcelBooking(UUID parcelId, UUID currentUserId);

    BookingRequestDto getBookingRequest(UUID requestId, UUID currentUserId);

    List<BookingRequestDto> getBookingRequests(UUID currentUserId);

    BookingRequestDto createBookingRequest(BookingCreateRequest dto, UUID senderId);

    void cancelRequest(UUID requestId, UUID userId);

    void acceptRequest(UUID requestId, UUID carrierId);

    void rejectRequest(UUID requestId, UUID carrierId, String reason);

    void cancelBooking(UUID bookingId, UUID userId);

    void pay(UUID bookingId, UUID senderId);

    void complete(UUID bookingId, UUID carrierId);
}