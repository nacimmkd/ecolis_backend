package com.deliveryplatform.users;

import com.deliveryplatform.common.ErrorDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserDto> getMe(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        var userDto = userService.findById(principal.getId());
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        var user = userService.register(request);

        var uri = uriBuilder
                .path("/users/{id}")
                .build(user.id());

        return ResponseEntity.created(uri).body(user);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateMe(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProfileRequest request
    ) {
        var updatedUserDto = userService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(updatedUserDto);
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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(PasswordNotValidException.class)
    public ResponseEntity<ErrorDto> handlePasswordNotValidException(PasswordNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleEmailAlreadyExistsException(EmailAlreadyExistsException    ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto(ex.getMessage()));
    }

    // ----------------------------------------------------------------
    // ADMIN
    // ----------------------------------------------------------------

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        var users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        var userDto = userService.findById(id);
        return ResponseEntity.ok(userDto);
    }


    @PutMapping("/{id}/ban")
    public ResponseEntity<Void> banUser(@PathVariable UUID id) {
        userService.banUser(id);
        return ResponseEntity.noContent().build();
    }

}
