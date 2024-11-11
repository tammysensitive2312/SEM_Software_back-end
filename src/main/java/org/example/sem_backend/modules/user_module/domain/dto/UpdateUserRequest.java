package org.example.sem_backend.modules.user_module.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    // Getters và Setters
    @NotBlank(message = "Username không thể để trống.")
    @Size(max = 20, message = "Username không được vượt quá 20 ký tự.")
    private String username;

    @Size(min = 8, message = "Password phải có ít nhất 8 ký tự.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)[^+\\-x:]*$",
            message = "Password phải có ít nhất một chữ hoa, một chữ thường, một ký tự đặc biệt và không chứa các ký tự +, -, x, :"
    )
    private String password;
}
