package org.example.sem_backend.modules.user_module.repository;

import org.example.sem_backend.modules.user_module.domain.entity.RefreshToken;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);

    void deleteByExpiryDateBefore(Instant now);
}
