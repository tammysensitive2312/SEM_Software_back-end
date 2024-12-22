package org.example.sem_backend.modules.user_module.domain.dto;

import lombok.*;
import org.example.sem_backend.modules.user_module.domain.entity.UserPreference;


/**
 * DTO for {@link UserPreference}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserPreferenceDto {
    private boolean emailNotification;
    private boolean inAppNotification;
    private boolean pushNotification;
}