package org.example.sem_backend.main_service.middleware.auth.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
//    @ValidPassword(message = "Password must contain at least one uppercase letter, one lowercase letter, one special character, and no spaces")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;
}