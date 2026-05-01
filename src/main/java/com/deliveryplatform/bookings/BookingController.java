package com.deliveryplatform.bookings;

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