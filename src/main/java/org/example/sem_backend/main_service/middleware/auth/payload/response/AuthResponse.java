package org.example.sem_backend.main_service.middleware.auth.payload.response;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class AuthResponse {
    private String jwtCookie;
    private String refreshCookie;
    private UserResponse userInfo;
}
