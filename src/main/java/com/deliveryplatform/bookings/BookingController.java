package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.bookings.dto.BookingCreateRequest;
import com.deliveryplatform.bookings.dto.BookingRequestDto;
import com.deliveryplatform.users.UserPrincipal;
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


    // ─── BOOKING ────────────────────────────────────────────────────
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal UserPrincipal user) {
        return bookingService.getBooking(bookingId, user.getId());
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
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.cancelBooking(bookingId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{bookingId}/complete")
    public ResponseEntity<Void> completeBooking(
            @PathVariable UUID bookingId,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.complete(bookingId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // ─── BOOKING REQUESTS ────────────────────────────────────────────────────

    @GetMapping("/requests/{requestId}")
    public ResponseEntity<BookingRequestDto> getBookingRequest(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal UUID currentUserId
    ) {
        return ResponseEntity.ok(bookingService.getBookingRequests(requestId, currentUserId));
    }

    @PostMapping("/requests")
    public ResponseEntity<BookingRequestDto> requestBooking(
            @RequestBody BookingCreateRequest dto,
            @AuthenticationPrincipal UUID currentUserId,
            UriComponentsBuilder uriBuilder
    ) {
        var request = bookingService.requestBooking(dto, currentUserId);
        var uri = uriBuilder
                .path("/api/v1/users/{id}")
                .build(request.requestId());
        return ResponseEntity.created(uri).body(request);
    }

    @PatchMapping("/requests/{requestId}/accept")
    public ResponseEntity<BookingDto> acceptRequest(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal UUID currentUserId
    ) {
        bookingService.acceptRequest(requestId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/requests/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable UUID requestId,
            @RequestParam String reason,
            @AuthenticationPrincipal UUID currentUserId
    ) {
        bookingService.rejectRequest(requestId, currentUserId, reason);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ResponseEntity<Void> cancelRequest(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal UUID currentUserId
    ) {
        bookingService.cancelRequest(requestId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}