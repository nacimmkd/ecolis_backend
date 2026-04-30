package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingRequestCreateRequest;
import com.deliveryplatform.bookings.dto.BookingRequestResponse;
import com.deliveryplatform.bookings.dto.BookingResponse;
import com.deliveryplatform.users.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;


    @GetMapping("/{id}")
    public BookingResponse getBooking(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {
        return bookingService.getBooking(id, user.getId());
    }

    @GetMapping("/requests/{id}")
    public BookingRequestResponse getBookingRequest(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {
        return bookingService.getBookingRequest(id, user.getId());
    }


    @PostMapping("/requests")
    public BookingRequestResponse createRequest(
            @RequestBody BookingRequestCreateRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        return bookingService.createRequest(request, user.getId());
    }

    @PatchMapping("/requests/{id}/cancel")
    public ResponseEntity<Void> cancelRequest(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.cancelRequest(id, user.getId());
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/requests/{id}/accept")
    public BookingResponse acceptRequest(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {
        return bookingService.acceptRequest(id, user.getId());
    }

    @PatchMapping("/requests/{id}/reject")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.rejectRequest(id, user.getId(), reason);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.cancelBooking(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<Void> pay(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.pay(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> complete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.complete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}