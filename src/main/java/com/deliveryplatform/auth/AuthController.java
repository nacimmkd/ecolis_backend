package com.deliveryplatform.auth;

import com.deliveryplatform.users.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;
    private final RefreshTokenService cacheService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                ));
        // access Token
        var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(UserNotFoundException::new);
        var accessToken = jwtService.generateAccessToken(user);

        //refresh token
        var refreshToken = jwtService.generateRefreshToken(user);
        cacheService.saveRefreshToken(user.getId(),refreshToken);
        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/auth");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok().body(new JwtResponse(accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(
            @CookieValue("refreshToken") String refreshToken
    ) {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userId = jwtService.getUserIdFromToken(refreshToken);
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        var accessToken = jwtService.generateAccessToken(user);
        return ResponseEntity.ok().body(new JwtResponse(accessToken));
    }


    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<Void> logout(
            @CookieValue("refreshToken")String refreshToken
    ) {
        var userId = jwtService.getUserIdFromToken(refreshToken);
        cacheService.removeRefreshToken(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<UserDto> register(
            @Valid @RequestBody RegisterRequest request,
            UriComponentsBuilder uriBuilder
    ){
        var user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .profile(null)
                .build();
        userRepository.save(user);
        var uri = uriBuilder.path("/companies").build().toUri();
        return ResponseEntity.created(uri).body(userMapper.toDto(user));
    }

    @PostMapping("/validate")
    public boolean validateToken(@RequestHeader("Authorization") String authHeader){
        var token = authHeader.replace("Bearer ", "");
        return jwtService.validateAccessToken(token);
    }


    // to Solve the probleme of getting 403 eroor instead of 401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
