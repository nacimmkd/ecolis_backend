package com.deliveryplatform.users;


import com.deliveryplatform.users.dto.UpdatePasswordRequest;
import com.deliveryplatform.users.dto.UserRequest;
import com.deliveryplatform.users.dto.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;



import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImp userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var userDto = userService.findById(principal.getId());
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody UserRequest request,
            UriComponentsBuilder uriBuilder
    ){
        var user = userService.register(request);

        var uri = uriBuilder
                .path("/api/v1/users/{id}")
                .build(user.id());

        return ResponseEntity.created(uri).body(user);
    }


    @PostMapping("/{id}/verification/send")
    public ResponseEntity<Void> sendVerificationCode(@PathVariable UUID id) {
        userService.sendVerificationCode(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/verification/verify")
    public ResponseEntity<Void> verify(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @NotBlank String code) {
        userService.verify(principal.getEmail(), code);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        userService.changePassword(principal.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        userService.softDelete(principal.getId());
        return ResponseEntity.noContent().build();
    }


    // ----------------------------------------------------------------
    // ADMIN
    // ----------------------------------------------------------------

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        var users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        var userDto = userService.findById(id);
        return ResponseEntity.ok(userDto);
    }


    @PutMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> banUser(@PathVariable UUID id) {
        userService.banUser(id);
        return ResponseEntity.noContent().build();
    }

}
