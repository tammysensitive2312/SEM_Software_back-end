package org.example.sem_backend.modules.user_module.repository;

import jakarta.persistence.QueryHint;
import org.example.sem_backend.modules.user_module.domain.entity.ERole;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("select distinct u.email from users u where u.id IN :userIds")
    List<String> findDistinctEmailsById(List<Long> userIds);

    @Query("select u.id from users u where u.role = :role")
    @QueryHints({
            @QueryHint(name = "org.hibernate.readOnly", value = "true"),
            @QueryHint(name ="org.hibernate.cacheable", value = "true"),
            @QueryHint(name ="jakarta.persistence.cache.retrieveMode", value = "USE"),
            @QueryHint(name ="jakarta.persistence.cache.storeMode", value = "USE")
    })
    List<Long> findIdByRole(ERole role);
}