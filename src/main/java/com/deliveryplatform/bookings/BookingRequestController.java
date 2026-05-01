package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingRequestCreateRequest;
import com.deliveryplatform.bookings.dto.BookingRequestResponse;
import com.deliveryplatform.bookings.dto.BookingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;



@RestController
@RequestMapping("/api/v1/booking-requests")
@RequiredArgsConstructor
public class BookingRequestController {

    private final BookingService bookingService;

    @GetMapping("/{requestId}")
    public ResponseEntity<BookingRequestResponse> getBookingRequest(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal UUID currentUserId
    ) {
        return ResponseEntity.ok(bookingService.getBookingRequest(requestId, currentUserId));
    }

    @PostMapping
    public ResponseEntity<BookingRequestResponse> createRequest(
            @RequestBody BookingRequestCreateRequest dto,
            @AuthenticationPrincipal UUID currentUserId,
            UriComponentsBuilder uriBuilder
    ) {
        var request = bookingService.createRequest(dto, currentUserId);
        var uri = uriBuilder
                .path("/api/v1/users/{id}")
                .build(request.requestId());
        return ResponseEntity.created(uri).body(request);
    }

    @PatchMapping("/{requestId}/accept")
    public ResponseEntity<BookingResponse> acceptRequest(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal UUID currentUserId
    ) {
        return ResponseEntity.ok(bookingService.acceptRequest(requestId, currentUserId));
    }

    @PatchMapping("/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable UUID requestId,
            @RequestParam String reason,
            @AuthenticationPrincipal UUID currentUserId
    ) {
        bookingService.rejectRequest(requestId, currentUserId, reason);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<Void> cancelRequest(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal UUID currentUserId
    ) {
        bookingService.cancelRequest(requestId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
