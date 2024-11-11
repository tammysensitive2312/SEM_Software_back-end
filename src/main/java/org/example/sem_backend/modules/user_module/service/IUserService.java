package org.example.sem_backend.modules.user_module.service;

import org.example.sem_backend.modules.user_module.domain.dto.UpdateUserRequest;
import org.example.sem_backend.modules.user_module.domain.dto.UserDto;

public interface IUserService {
    UserDto getUserById(Long userId);
    void addNewUser(UserDto user);
    void updateUserInfo(Long userId, UpdateUserRequest user);
}
