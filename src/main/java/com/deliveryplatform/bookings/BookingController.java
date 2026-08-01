package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.bookings.dto.CreateBookingRequest;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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

    @PostMapping
    public ResponseEntity<BookingDto> create(
            @RequestBody @Valid CreateBookingRequest dto,
            @AuthenticationPrincipal UserPrincipal user,
            UriComponentsBuilder uriBuilder) {
        var booking = bookingService.createBooking(dto, user.getId());
        var uri = uriBuilder
                .path("/api/v1/bookings/{id}")
                .build(booking.bookingId());
        return ResponseEntity.created(uri).body(booking);
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID bookingId,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.cancel(bookingId, reason, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{bookingId}/accept")
    public ResponseEntity<Void> acceptBooking(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.acceptBooking(bookingId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{bookingId}/reject")
    public ResponseEntity<Void> rejectBooking(
            @PathVariable UUID bookingId,
            @RequestParam String reason,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.rejectBooking(bookingId, user.getId(), reason);
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

    @PatchMapping("/{bookingId}/complete")
    public ResponseEntity<Void> complete(
            @PathVariable UUID bookingId,
            @RequestParam String code,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.complete(bookingId, code, user.getId());
        return ResponseEntity.noContent().build();
    }
}
