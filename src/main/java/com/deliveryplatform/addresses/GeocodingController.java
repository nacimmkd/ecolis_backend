package com.deliveryplatform.addresses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// NOTE : Controller just for testing

@RestController
@RequestMapping("/api/v1/geocoding")
@RequiredArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;

    @PostMapping("/geocode")
    public ResponseEntity<GeocodedAddress> geocode(@Valid @RequestBody Address address) {
        GeocodedAddress result = geocodingService.geocode(address);
        return ResponseEntity.ok(result);
    }
}