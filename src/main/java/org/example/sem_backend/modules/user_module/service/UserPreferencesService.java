package org.example.sem_backend.modules.user_module.service;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.user_module.domain.dto.UserPreferenceDto;
import org.example.sem_backend.modules.user_module.domain.entity.UserPreference;
import org.example.sem_backend.modules.user_module.domain.mapper.UserPreferenceMapper;
import org.example.sem_backend.modules.user_module.repository.UserPreferenceRepository;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPreferencesService {
    private final UserPreferenceRepository repository;
    private final UserPreferenceMapper mapper;
    private final UserRepository userRepository;

    public UserPreferenceDto getPreference(Long userId) {
        return repository.findByUserId(userId)
                .map(mapper::toDto)
                .orElseThrow();
    }

    @Transactional
    public void updatePartialPreferences(Long userId, UserPreferenceDto request) {
        UserPreference preferences = repository.findByUserId(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with ID: " + userId, "USER-MODULE"));
        preferences = mapper.partialUpdate(request, preferences);

        repository.save(preferences);
    }

    private UserPreference createDefaultPreference(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId, "USER-MODULE"));


        UserPreference preferences = new UserPreference();
        preferences.setUser(user);
        preferences.setInAppNotification(true);
        preferences.setEmailNotification(true);
        preferences.setPushNotification(false);
        return repository.save(preferences);
    }

}
