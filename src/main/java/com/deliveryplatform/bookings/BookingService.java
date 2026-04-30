package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingRequestCreateRequest;
import com.deliveryplatform.bookings.dto.BookingRequestResponse;
import com.deliveryplatform.bookings.dto.BookingResponse;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    BookingResponse getBooking(UUID bookingId, UUID currentUserId);

    BookingRequestResponse getBookingRequest(UUID requestId, UUID currentUserId);

    List<BookingRequestResponse> getTripRequests(UUID tripId, UUID currentUserId);

    List<BookingRequestResponse> getParcelRequests(UUID parcelId, UUID currentUserId);

    BookingRequestResponse createRequest(BookingRequestCreateRequest dto, UUID senderId);

    void cancelRequest(UUID requestId, UUID userId);

    BookingResponse acceptRequest(UUID requestId, UUID carrierId);

    void rejectRequest(UUID requestId, UUID carrierId, String reason);

    void cancelBooking(UUID bookingId, UUID userId);

    void pay(UUID bookingId, UUID senderId);

    void complete(UUID bookingId, UUID carrierId);
}