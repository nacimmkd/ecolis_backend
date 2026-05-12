package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.users.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal UserPrincipal user) {
        return bookingService.getBooking(bookingId, user.getId());
    }

    @GetMapping("/me")
    public ResponseEntity<List<BookingDto>> getMyBookings(
            @RequestParam(required = false) UUID tripId,
            @RequestParam(required = false) UUID parcelId,
            @AuthenticationPrincipal UserPrincipal user
    ) throws BadRequestException {

        if (tripId != null && parcelId != null)
            throw new BadRequestException("tripId and parcelId cannot be presented at same time");

        List<BookingDto> bookings;

        if (tripId != null) {
            bookings = bookingService.getTripBookings(tripId, user.getId());
        } else if (parcelId != null) {
            BookingDto booking = bookingService.getParcelBooking(parcelId, user.getId());
            bookings = (booking != null) ? List.of(booking) : List.of();
        } else {
            bookings = bookingService.getMyBookings(user.getId());
        }

        return ResponseEntity.ok(bookings);
    }

    @PatchMapping("/{bookingId}/pay")
    public ResponseEntity<Void> payBooking(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.pay(bookingId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable UUID bookingId,
            @RequestParam String reason,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.cancel(bookingId,reason, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{bookingId}/complete")
    public ResponseEntity<Void> completeBooking(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.complete(bookingId, user.getId());
        return ResponseEntity.noContent().build();
    }
}