package com.deliveryplatform.parcels;

import com.deliveryplatform.users.User;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.deliveryplatform.parcels.ParcelDto.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parcels")
@RequiredArgsConstructor
public class ParcelController {

    private final ParcelService parcelService;

    @GetMapping("/{id}") // public
    public ResponseEntity<ParcelResponse> getParcel(@PathVariable UUID id) {
        return ResponseEntity.ok(parcelService.getParcel(id));
    }

    @GetMapping("/{id}/secure") // private
    public ResponseEntity<ParcelWithCodeResponse> getParcelWithCode(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(parcelService.getParcelWithCode(id, user.getId()));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ParcelWithCodeResponse>> getMyParcels(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(parcelService.getUserParcels(userPrincipal.getId()));
    }

    @GetMapping("/available") // public
    public ResponseEntity<List<ParcelResponse>> getAvailableParcels() {
        return ResponseEntity.ok(parcelService.getAvailableParcels());
    }


    @PostMapping
    public ResponseEntity<ParcelWithCodeResponse> createParcel(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid ParcelRequest request,
            UriComponentsBuilder uriBuilder) {

        var parcelDto = parcelService.createParcel(
                userPrincipal.getId(),
                request
        );
        var uri = uriBuilder.path("/api/v1/parcels/{id}").build(parcelDto.id());
        return ResponseEntity.created(uri).body(parcelDto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ParcelWithCodeResponse> updateParcel(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid ParcelRequest request) {

        return ResponseEntity.ok(parcelService.updateParcel(id, userPrincipal.getId(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcel(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        parcelService.deleteParcel(id, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }


    // ---------------ADMIN--------------------------

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ParcelResponse>> getParcels() {
        return ResponseEntity.ok(parcelService.getParcels());
    }

}
