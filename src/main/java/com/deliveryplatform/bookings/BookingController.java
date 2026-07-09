package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.constraints.NotNull;
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

    @GetMapping
    public ResponseEntity<?> getBookings(
            @RequestParam(required = false) UUID tripId,
            @RequestParam(required = false) UUID parcelId,
            @AuthenticationPrincipal UserPrincipal user
    ) throws BadRequestException {

        if (tripId != null && parcelId != null)
            throw new BadRequestException("tripId and parcelId cannot be presented at same time");

        if (tripId != null) {
            return ResponseEntity.ok(bookingService.getTripBookings(tripId, user.getId()));
        } else if (parcelId != null) {
            return ResponseEntity.ok(bookingService.getParcelBooking(parcelId, user.getId()));
        } else {
            return ResponseEntity.ok(bookingService.getMyBookings(user.getId()));
        }
    }

    @PatchMapping("/{bookingId}/pay")
    public ResponseEntity<Void> pay(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.pay(bookingId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{bookingId}/confirm-pickup")
    public ResponseEntity<Void> confirmPickUp(
            @PathVariable UUID bookingId,
            @RequestParam @NotNull String code,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.confirmPickUp(bookingId, code, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID bookingId,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.cancel(bookingId, reason, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{bookingId}/complete")
    public ResponseEntity<Void> complete(
            @PathVariable UUID bookingId,
            @RequestParam String code,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.complete(bookingId, code, user.getId());
        return ResponseEntity.noContent().build();
    }
}