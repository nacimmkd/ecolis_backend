package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingCreateRequest;
import com.deliveryplatform.bookings.dto.BookingRequestDto;
import com.deliveryplatform.bookings.dto.BookingDto;

import java.util.UUID;

public interface BookingService {

    BookingDto getBooking(UUID bookingId, UUID currentUserId);

    BookingRequestDto getBookingRequests(UUID requestId, UUID currentUserId);

    BookingRequestDto requestBooking(BookingCreateRequest dto, UUID senderId);

    void cancelRequest(UUID requestId, UUID userId);

    void acceptRequest(UUID requestId, UUID carrierId);

    void rejectRequest(UUID requestId, UUID carrierId, String reason);

    void cancelBooking(UUID bookingId, UUID userId);

    void pay(UUID bookingId, UUID senderId);

    void complete(UUID bookingId, UUID carrierId);
}