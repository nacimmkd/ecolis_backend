package com.deliveryplatform.trips;

import com.deliveryplatform.trips.dto.TripStopRequest;
import com.deliveryplatform.trips.dto.TripStopResponse;
import com.deliveryplatform.trips.dto.TripCreateRequest;
import com.deliveryplatform.trips.dto.TripResponse;
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


    // PUBLIC

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTrip(@PathVariable UUID id) {
        return ResponseEntity.ok(tripService.getTrip(id));
    }

    // USER

    @GetMapping("/me")
    public ResponseEntity<List<TripResponse>> getMyTrips(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(tripService.getUserTrips(principal.getId()));
    }

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TripCreateRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        var trip = tripService.createTrip(principal.getId(), request);
        var uri  = uriBuilder.path("/api/v1/trips/{id}").build(trip.tripId());
        return ResponseEntity.created(uri).body(trip);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TripCreateRequest request
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
    public TripStopResponse addStop(
            @PathVariable UUID tripId,
            @RequestBody TripStopRequest stopRequest,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return tripService.addStop(tripId, principal.getId(), stopRequest);
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


    @PatchMapping("/{tripId}/stops/{stopId}")
    public ResponseEntity<TripStopResponse> updateTripStop(
            @PathVariable UUID tripId,
            @PathVariable UUID stopId,
            @RequestBody TripStopRequest stopRequest,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                tripService.updateStop(stopId, tripId, principal.getId(), stopRequest)
        );
    }



    // ADMIN

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TripResponse>> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }


}