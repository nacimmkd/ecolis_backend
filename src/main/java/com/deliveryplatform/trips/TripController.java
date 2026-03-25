package com.deliveryplatform.trips;

import com.deliveryplatform.common.addresses.Address;
import com.deliveryplatform.trips.TripDto.*;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.deliveryplatform.trips.TripStopDto.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    // ----------------------------------------------------------------
    // PUBLIC
    // ----------------------------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTrip(@PathVariable UUID id) {
        return ResponseEntity.ok(tripService.getTrip(id));
    }

    @GetMapping("/available")
    public ResponseEntity<List<TripResponse>> getAvailableTrips() {
        return ResponseEntity.ok(tripService.getAvailableTrips());
    }

    // ----------------------------------------------------------------
    // USER
    // ----------------------------------------------------------------

    @GetMapping("/me")
    public ResponseEntity<List<TripResponse>> getMyTrips(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(tripService.getUserTrips(principal.getId()));
    }

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TripRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        var trip = tripService.createTrip(principal.getId(), request);
        var uri  = uriBuilder.path("/api/v1/trips/{id}").build(trip.id());
        return ResponseEntity.created(uri).body(trip);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TripRequest request
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


    // ----------------------------------------------------------------
    // STOPS
    // ----------------------------------------------------------------


    @PostMapping("/{tripId}/stops")
    public StopResponse addStop(
            @PathVariable UUID tripId,
            @RequestBody Address address,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return tripService.addStop(tripId, principal.getId(), address);
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


    @PutMapping("/{tripId}/stops/{stopId}")
    public ResponseEntity<StopResponse> updateTripStop(
            @PathVariable UUID tripId,
            @PathVariable UUID stopId,
            @RequestBody Address address,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                tripService.updateStop(stopId, tripId, principal.getId(), address)
        );
    }



    // ----------------------------------------------------------------
    // ADMIN
    // ----------------------------------------------------------------

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TripResponse>> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID id,
            @RequestParam TripStatus status
    ) {
        tripService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }


}