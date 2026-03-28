package com.deliveryplatform.parcels;

import com.deliveryplatform.parcels.dto.ParcelRequest;
import com.deliveryplatform.parcels.dto.ParcelResponse;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
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

    @GetMapping("/{id}/confirmation-code") // private
    public ResponseEntity<Map<String,String>> getConfirmationCode(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(
                Map.of("codeOTP",parcelService.getConfirmationCode(id,user.getId())
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ParcelResponse>> getMyParcels(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(parcelService.getUserParcels(userPrincipal.getId()));
    }


    @PostMapping
    public ResponseEntity<ParcelResponse> createParcel(
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
    public ResponseEntity<ParcelResponse> updateParcel(
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
