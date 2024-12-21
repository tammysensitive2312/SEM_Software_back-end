package org.example.sem_backend.modules.user_module.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;

}
