package org.example.sem_backend.main_service.middleware.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.exception.InvalidCredentialsException;
import org.example.sem_backend.main_service.middleware.auth.payload.request.LoginRequest;
import org.example.sem_backend.main_service.middleware.auth.payload.request.SignupRequest;
import org.example.sem_backend.main_service.middleware.auth.payload.response.AuthResponse;
import org.example.sem_backend.main_service.middleware.auth.payload.response.MessageResponse;
import org.example.sem_backend.main_service.middleware.auth.payload.response.UserResponse;
import org.example.sem_backend.main_service.middleware.auth.jwt.JwtUtils;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.common_module.exception.TokenRefreshException;
import org.example.sem_backend.modules.user_module.domain.entity.RefreshToken;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse authenticateUser(LoginRequest loginRequest) throws BadCredentialsException {
        if (!userRepository.existsByUsername(loginRequest.getUsername())) {
            throw new ResourceNotFoundException("User not found with provided info", "Auth-Controller");
        }

        try {
            // Thực hiện xác thực
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Tạo JWT và refresh token cookie
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails.getUsername(), userDetails.getEmail());
            ResponseCookie refreshCookie = jwtUtils.generateRefreshJwtCookie(
                    refreshTokenService.createRefreshToken(userDetails.getId()).getToken()
            );

            // Tạo phản hồi xác thực
            AuthResponse response = new AuthResponse();
            response.setJwtCookie(jwtCookie.toString());
            response.setRefreshCookie(refreshCookie.toString());
            response.setUserInfo(new UserResponse(
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getRole().name()
            ));

            return response;

        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid username or password", "Auth-Controller");
        }
    }

    public MessageResponse registerUser(SignupRequest request) {
        MessageResponse response = new MessageResponse();

        if (userRepository.existsByUsername(request.getUsername())) {
            response.setMessage("Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            response.setMessage("Email is already in use!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);
        response.setMessage("User registered successfully!");

        return response;
    }

    public Pair<String, String> logoutUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(principal.toString(), "anonymousUser")) {
            refreshTokenService.deleteByUserId(((UserDetailsImpl) principal).getId());
        }

        ResponseCookie cleanJwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie cleanRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        return Pair.of(cleanJwtCookie.toString(), cleanRefreshCookie.toString());
    }

    public String refreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

        if (StringUtils.hasText(refreshToken)) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user.getUsername(), user.getEmail());
                        return jwtCookie.toString();
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token is not in database!"));
        }
        return null;
    }
}
