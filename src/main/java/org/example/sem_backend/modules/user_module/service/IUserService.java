package org.example.sem_backend.modules.user_module.service;

import org.example.sem_backend.modules.user_module.domain.dto.UserDto;

public interface IUserService {
    UserDto getUserById(Long userId);
    Boolean addNewUser(UserDto user);
    Boolean updateUserInfo(UserDto user);
}
