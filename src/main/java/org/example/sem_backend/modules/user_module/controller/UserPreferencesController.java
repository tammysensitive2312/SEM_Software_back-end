package org.example.sem_backend.modules.user_module.controller;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.user_module.domain.dto.UserPreferenceDto;
import org.example.sem_backend.modules.user_module.service.UserPreferencesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/preferences")
@RequiredArgsConstructor
public class UserPreferencesController {
    private final UserPreferencesService preferencesService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserPreferenceDto> getUserPreferences(@PathVariable Long userId) {
        UserPreferenceDto preferences = preferencesService.getPreference(userId);
        return ResponseEntity.ok(preferences);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> updatePartialPreferences(
            @PathVariable Long userId,
            @RequestBody UserPreferenceDto request
    ) {
        preferencesService.updatePartialPreferences(userId, request);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
