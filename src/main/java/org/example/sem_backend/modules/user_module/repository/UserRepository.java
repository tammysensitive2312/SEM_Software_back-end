package org.example.sem_backend.modules.user_module.repository;

import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}