package org.example.sem_backend.modules.user_module.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.user_module.domain.dto.UpdateUserRequest;
import org.example.sem_backend.modules.user_module.domain.dto.UserDto;
import org.example.sem_backend.modules.user_module.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
        UserDto dto = userService.getUserById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping("/new")
    public ResponseEntity<?> addNewUser(@RequestBody @Valid UserDto userDto) {
        userService.addNewUser(userDto);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@NotNull @PathVariable("id") Long id, @Valid @RequestBody UpdateUserRequest userDto) {
        userService.updateUserInfo(id, userDto);
        return ResponseEntity.ok().body(userDto);
    }
}
