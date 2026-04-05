package com.deliveryplatform.addresses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/geocoding")
@RequiredArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;

    @PostMapping("/geocode")
    public ResponseEntity<GeocodedAddress> geocode(@Valid @RequestBody Address address) {
        GeocodedAddress result = geocodingService.geocode(address);
        return ResponseEntity.ok(result);
    }
}