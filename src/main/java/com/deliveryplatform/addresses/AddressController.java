package com.deliveryplatform.addresses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// NOTE : Controller just for testing
@RestController
@RequestMapping("/api/v1/geocoding")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/geocode")
    public ResponseEntity<Address> geocode(@Valid @RequestBody AddressRequest address) {
        Address result = addressService.geocode(address);
        return ResponseEntity.ok(result);
    }
}