package com.deliveryplatform.users;


import com.deliveryplatform.users.dto.*;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDetails> getMe(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var userDto = userService.findById(principal.getId());
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDetails> register(
            @Valid @RequestBody UserCreateRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        var user = userService.register(request);

        var uri = uriBuilder
                .path("/api/v1/users/{id}")
                .build(user.userId());

        return ResponseEntity.created(uri).body(user);
    }


    @PostMapping("/verification/request")
    public ResponseEntity<Void> requestVerification(
            @RequestBody RequestEmailVerification request
    ) {
        userService.requestEmailVerification(request.email());
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/verification/verify")
    public ResponseEntity<Void> verify(@RequestBody @Valid VerifyEmailRequest request) {
        userService.verifyEmail(request.token());
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/password/reset/request")
    public ResponseEntity<Void> requestPasswordReset(
            @Valid @RequestBody RequestPasswordReset request
    ) {
        userService.requestPasswordReset(request.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        userService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        userService.updatePassword(principal.getId(), request);
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
    public ResponseEntity<List<UserSummary>> getAllUsers() {
        var users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetails> getUserById(@PathVariable UUID id) {
        var userDto = userService.findById(id);
        return ResponseEntity.ok(userDto);
    }

}
