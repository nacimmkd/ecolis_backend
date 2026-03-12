package com.deliveryplatform.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(userService.findById(principal.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateMe(
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
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @PageableDefault(size = 20, sort = "registeredAt") Pageable pageable
    ) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }


    @PutMapping("/{id}/ban")
    public ResponseEntity<Void> banUser(@PathVariable UUID id) {
        userService.banUser(id);
        return ResponseEntity.noContent().build();
    }
}
