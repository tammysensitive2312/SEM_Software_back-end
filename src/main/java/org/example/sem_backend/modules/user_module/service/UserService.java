package org.example.sem_backend.modules.user_module.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.user_module.domain.dto.PasswordChangeRequest;
import org.example.sem_backend.modules.user_module.domain.dto.UserDto;
import org.example.sem_backend.modules.user_module.domain.dto.UserUpdateRequest;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.example.sem_backend.modules.user_module.domain.mapper.UserMapper;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void updateUser(UserUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User is not authenticated");
        }

        String currentEmail = authentication.getName();

        // Lấy thông tin người dùng hiện tại từ database
        Optional<User> optionalUser = userRepository.findByEmail(currentEmail);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(request.getUsername());
            userRepository.save(user);
        } else {
            throw new ResourceNotFoundException("User not found", "User-Service");
        }
    }

    @Override
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        Optional<User> optionalUser = userRepository.findByEmail(currentEmail);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return userMapper.toDto(user);
        } else {
            throw new ResourceNotFoundException("User not found", "User-Service");
        }
    }

    @Override
    public void changePassword(PasswordChangeRequest passwordChangeRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        Optional<User> optionalUser = userRepository.findByEmail(currentEmail);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
                userRepository.save(user);
            } else {
                throw new RuntimeException("Old password is incorrect");
            }
        } else {
            throw new ResourceNotFoundException("User not found", "User-Service");
        }
    }
}
