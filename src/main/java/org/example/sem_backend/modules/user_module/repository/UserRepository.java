package org.example.sem_backend.modules.user_module.repository;

import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("select distinct u.email from users u where u.id IN :userIds")
    List<String> findDistinctEmailsById(List<Long> userIds);
}