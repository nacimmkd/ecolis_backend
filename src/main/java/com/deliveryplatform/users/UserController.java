package com.deliveryplatform.users;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ----------------------------------------------------------------
    // MY PROFILE
    // ----------------------------------------------------------------

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(userService.findById(principal.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(principal.getId(), request));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request
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
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 20, sort = "registeredAt") Pageable pageable
    ) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }


    @PutMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> banUser(@PathVariable UUID id) {
        userService.banUser(id);
        return ResponseEntity.noContent().build();
    }
}
