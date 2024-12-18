package org.example.sem_backend.modules.notification_module.repository;

import org.example.sem_backend.modules.notification_module.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientsContainingAndIsReadFalse(Long userId);

//    long countByRecipientsContainingAndIsReadFalse(Long userId);

    Page<Notification> findByRecipientsContaining(Long userId, Pageable pageable);
}