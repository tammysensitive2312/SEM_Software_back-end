package org.example.sem_backend.modules.user_module.service;

import org.example.sem_backend.modules.user_module.domain.dto.PasswordChangeRequest;
import org.example.sem_backend.modules.user_module.domain.dto.UserDto;
import org.example.sem_backend.modules.user_module.domain.dto.UserUpdateRequest;

public interface IUserService {
    void updateUser(UserUpdateRequest request);

    UserDto getCurrentUser();

    void changePassword(PasswordChangeRequest passwordChangeRequest);
}
