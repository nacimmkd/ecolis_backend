package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.AddressRequest;
import com.deliveryplatform.trips.dto.*;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
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
    public ResponseEntity<List<TripSummary>> getMyTrips(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(tripService.getMyTrips(principal.getId()));
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
    @PatchMapping("/{tripId}/stops")
    public ResponseEntity<Void> addStop(
            @PathVariable UUID tripId,
            @RequestBody AddressRequest address,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        tripService.addStop(tripId, principal.getId(), address);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{tripId}/stops")
    public ResponseEntity<List<TripStopDto>> updateTripStops(
            @PathVariable UUID tripId,
            @RequestBody List<TripStopRequest> newStops,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                tripService.updateStops(tripId, principal.getId(), newStops)
        );
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
    public ResponseEntity<List<TripSummary>> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }


}