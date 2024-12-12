package org.example.sem_backend.main_service.middleware.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.main_service.middleware.auth.payload.request.LoginRequest;
import org.example.sem_backend.main_service.middleware.auth.payload.request.SignupRequest;
import org.example.sem_backend.main_service.middleware.auth.payload.response.AuthResponse;
import org.example.sem_backend.main_service.middleware.auth.payload.response.MessageResponse;
import org.example.sem_backend.main_service.middleware.auth.payload.response.UserResponse;
import org.example.sem_backend.main_service.middleware.auth.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = service.authenticateUser(loginRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.getJwtCookie())
                .header(HttpHeaders.SET_COOKIE, response.getRefreshCookie())
                .body(new UserResponse(
                        response.getUserInfo().getId(),
                        response.getUserInfo().getUsername(),
                        response.getUserInfo().getEmail(),
                        response.getUserInfo().getRole()
                ));
    }


    @PostMapping("/sign-up")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        MessageResponse response = service.registerUser(signUpRequest);
        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<String> logoutUser() {
        String cleanJwtCookie = service.logoutUser().getFirst();
        String cleanRefreshCookie = service.logoutUser().getSecond();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanJwtCookie)
                .header(HttpHeaders.SET_COOKIE, cleanRefreshCookie)
                .body("You've been signed out!");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshTokenCookie = service.refreshToken(request);
        if (refreshTokenCookie.isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh token is required");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie)
                .body(new MessageResponse("Token refreshed successfully!"));
    }
}
