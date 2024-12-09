package org.example.sem_backend.common_module.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.auth.payload.request.LoginRequest;
import org.example.sem_backend.common_module.auth.payload.request.SignupRequest;
import org.example.sem_backend.common_module.auth.payload.response.MessageResponse;
import org.example.sem_backend.common_module.auth.payload.response.UserResponse;
import org.example.sem_backend.common_module.auth.security.jwt.JwtUtils;
import org.example.sem_backend.common_module.auth.security.service.RefreshTokenService;
import org.example.sem_backend.common_module.auth.security.service.UserDetailsImpl;
import org.example.sem_backend.common_module.exception.TokenRefreshException;
import org.example.sem_backend.modules.user_module.domain.entity.RefreshToken;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Check if the username exists in the database
        if (!userRepository.existsByUsername(loginRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username not found"));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails.getUsername(), userDetails.getEmail());
            ResponseCookie refreshCookie = jwtUtils.generateRefreshJwtCookie(
                    refreshTokenService.createRefreshToken(userDetails.getId()).getToken()
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(new UserResponse(
                            userDetails.getId(),
                            userDetails.getUsername(),
                            userDetails.getEmail(),
                            userDetails.getRole().name()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Mật khẩu không đúng"));
        }
    }


    @PostMapping("/sign-up")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use!"));
        }

        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .role(signUpRequest.getRole())
                .build();
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<String> logoutUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(principal.toString(), "anonymousUser")) {
            refreshTokenService.deleteByUserId(((UserDetailsImpl) principal).getId());
        }

        ResponseCookie cleanJwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie cleanRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanJwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, cleanRefreshCookie.toString())
                .body("You've been signed out!");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

        if (refreshToken != null && !refreshToken.isEmpty()) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user.getUsername(), user.getEmail());
                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body(new MessageResponse("Token refreshed successfully!"));
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token is not in database!"));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Refresh token is required!"));
    }
}
