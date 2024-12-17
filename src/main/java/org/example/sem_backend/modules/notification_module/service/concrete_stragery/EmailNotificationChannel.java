package org.example.sem_backend.modules.notification_module.service.concrete_stragery;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.notification_module.domain.entity.Notification;
import org.example.sem_backend.modules.notification_module.service.stragery.NotificationChannel;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmailNotificationChannel implements NotificationChannel {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Override
    public void send(Notification notification) {
        String[] userEmail = exactEmailFromListRecipient(notification.getRecipients());
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(userEmail);
        email.setSubject("Thông báo mới từ SEM");
        email.setText(notification.getMessage());

        mailSender.send(email);
    }

    private String[] exactEmailFromListRecipient(Set<Long> recipients) {
        List<Long> userIds = recipients.stream().toList();
        String[] userEmail = userRepository.findDistinctEmailsById(userIds).toArray(new String[0]);
        return userEmail;
    }
}
