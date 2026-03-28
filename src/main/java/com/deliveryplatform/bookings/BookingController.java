package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingRequest;
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
    public BookingResponse getBooking(@PathVariable UUID id){
        return bookingService.getBookingById(id);
    }

    @PostMapping
    public BookingResponse create(
            @RequestBody BookingRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        return bookingService.create(request , user.getId());
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<Void> accept(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.accept(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> reject(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.reject(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {
        bookingService.cancel(id, user.getId());
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
