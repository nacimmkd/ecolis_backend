package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.AddressRequest;
import com.deliveryplatform.trips.dto.TripBookingDto;
import com.deliveryplatform.trips.dto.*;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripServiceImp tripService;


    @GetMapping("/{id}")
    public ResponseEntity<TripDetails> getTrip(@PathVariable UUID id) {
        return ResponseEntity.ok(tripService.getTrip(id));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<TripSummary>> getMyTrips(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) TripState tripState,
            @PageableDefault(size = 3, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(tripService.getMyTrips(principal.getId(), tripState, pageable));
    }

    @GetMapping("/{tripId}/bookings")
    public ResponseEntity<Page<TripBookingDto>> getTripBookings(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(tripService.getTripBookings(tripId, principal.getId(), pageable));
    }

    @GetMapping("/{tripId}/requests")
    public ResponseEntity<Page<TripBookingDto>> getTripRequests(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(tripService.getRequests(tripId, principal.getId(), pageable));
    }


    @PostMapping
    public ResponseEntity<TripDetails> createTrip(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TripCreateRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        var trip = tripService.createTrip(principal.getId(), request);
        var uri  = uriBuilder.path("/api/v1/trips/{id}").build(trip.tripId());
        return ResponseEntity.created(uri).body(trip);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripDetails> updateTrip(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TripUpdateRequest request
    ) {
        return ResponseEntity.ok(tripService.updateTrip(id, principal.getId(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        tripService.deleteTrip(id, principal.getId());
        return ResponseEntity.noContent().build();
    }

    // STOPS
    @PostMapping("/{tripId}/stops")
    public ResponseEntity<Void> addStop(
            @PathVariable UUID tripId,
            @RequestBody AddressRequest address,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        tripService.addStop(tripId, principal.getId(), address);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{tripId}/stops/{stopId}")
    public ResponseEntity<Void> deleteTripStop(
            @PathVariable UUID tripId,
            @PathVariable UUID stopId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        tripService.deleteStop(stopId,tripId, principal.getId());
        return ResponseEntity.noContent().build();
    }


    // ADMIN

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TripSummary>> getAllTrips(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(tripService.getAllTrips(pageable));
    }


}