package org.example.sem_backend.modules.user_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.main_service.middleware.auth.payload.response.MessageResponse;
import org.example.sem_backend.modules.user_module.domain.dto.PasswordChangeRequest;
import org.example.sem_backend.modules.user_module.domain.dto.UserDto;
import org.example.sem_backend.modules.user_module.domain.dto.UserUpdateRequest;
import org.example.sem_backend.modules.user_module.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get current user info")
    @GetMapping("/my-info")
    public ResponseEntity<UserDto> getCurrentUser() {
        UserDto userInfoResponse = userService.getCurrentUser();
        return ResponseEntity.ok(userInfoResponse);
    }

    @Operation(summary = "Update user info")
    @PutMapping("/update")
    public ResponseEntity<MessageResponse> updateUser(@Valid @RequestBody UserUpdateRequest request) {
        userService.updateUser(request);
        return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
    }

    @Operation(summary = "Change user password")
    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest) {
        userService.changePassword(passwordChangeRequest);
        return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
    }
}
