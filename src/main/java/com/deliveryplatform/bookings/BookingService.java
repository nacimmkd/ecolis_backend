package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.bookings.dto.CreateBookingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BookingService {

    BookingDto getBooking(UUID bookingId, UUID currentUserId);

    Page<BookingDto> getSentBookings(UUID currentUserId, Pageable pageable);

    Page<BookingDto> getReceivedBookings(UUID currentUserId, Pageable pageable);

    BookingDto getOrCreateBooking(CreateBookingRequest dto, UUID senderId);

    void requestDriver(UUID bookingId, UUID requesterId);

    void acceptBooking(UUID bookingId, UUID carrierId);

    void rejectBooking(UUID bookingId, UUID carrierId, String reason);

    void cancel(UUID bookingId, String reason, UUID userId);

    void confirmPickUp(UUID bookingId, String pickUpCode, UUID userId);

    void complete(UUID bookingId, String dropOfCode, UUID carrierId);
}
