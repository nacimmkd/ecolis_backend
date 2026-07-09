package com.deliveryplatform.parcels;

import com.deliveryplatform.parcels.dto.*;
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
@RequestMapping("/api/v1/parcels")
@RequiredArgsConstructor
public class ParcelController {

    private final ParcelService parcelService;

    @GetMapping("/{id}") // public
    public ResponseEntity<ParcelOwnerDto> getParcel(@PathVariable UUID id) {
        return ResponseEntity.ok(parcelService.getParcel(id));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ParcelSummaryDto>> getMyParcels(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(parcelService.getUserParcels(userPrincipal.getId()));
    }


    @PostMapping
    public ResponseEntity<ParcelOwnerDto> createParcel(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid ParcelCreateRequest request,
            UriComponentsBuilder uriBuilder) {

        var parcelDto = parcelService.createParcel(
                userPrincipal.getId(),
                request
        );
        var uri = uriBuilder.path("/api/v1/parcels/{id}").build(parcelDto.parcelId());
        return ResponseEntity.created(uri).body(parcelDto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ParcelOwnerDto> updateParcel(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid ParcelUpdateRequest request) {

        return ResponseEntity.ok(parcelService.updateParcel(id, userPrincipal.getId(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcel(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        parcelService.deleteParcel(id, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{parcelId}/tracking")
    public ResponseEntity<List<TrackEventDto>> getTrackingEvents(@PathVariable UUID parcelId) {
        return ResponseEntity.ok().body(parcelService.getTrackingEvents(parcelId));
    }


    // ---------------ADMIN--------------------------

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ParcelSummaryDto>> getParcels() {
        return ResponseEntity.ok(parcelService.getParcels());
    }

}
