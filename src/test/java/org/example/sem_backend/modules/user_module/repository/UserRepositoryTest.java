package org.example.sem_backend.modules.user_module.repository;

import org.example.sem_backend.modules.user_module.domain.entity.ERole;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Find by role success response")
    void findIdByRole_ShouldReturnUserIds() {
        User adminUser = new User(1L, "admin@example.com", "Admin", "User", ERole.ROLE_ADMIN);
        User anotherAdminUser = new User(2L, "another-admin@example.com", "Another", "Admin", ERole.ROLE_ADMIN);

        when(userRepository.findIdByRole(ERole.ROLE_ADMIN))
                .thenReturn(Arrays.asList(adminUser.getId(), anotherAdminUser.getId()));

        List<Long> adminIds = userRepository.findIdByRole(ERole.ROLE_ADMIN);

        assertEquals(2, adminIds.size());
        assertEquals(Arrays.asList(1L, 2L), adminIds);
    }
}