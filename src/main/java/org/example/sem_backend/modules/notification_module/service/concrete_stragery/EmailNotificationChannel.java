package org.example.sem_backend.modules.notification_module.service.concrete_stragery;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.notification_module.domain.entity.Notification;
import org.example.sem_backend.modules.notification_module.service.stragery.NotificationChannel;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmailNotificationChannel implements NotificationChannel {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final TemplateEngine templateEngine;

    @Override
    public void send(Notification notification) {
        Set<Long> recipientIds = notification.getRecipients();
        List<User> users = userRepository.findAllById(recipientIds);

        for (User user : users) {
            // Tạo context cho mỗi người nhận
            Context context = new Context();
            context.setVariable("message", notification.getMessage());
            context.setVariable("userName", user.getUsername());
            // Thêm biến tùy chỉnh khác nếu cần

            // Xử lý template thành HTML
            String htmlContent = templateEngine.process("notification-email", context);

            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                helper.setText(htmlContent, true); // true để gửi email dạng HTML
                helper.setTo(user.getEmail());
                helper.setSubject("Thông báo mới từ SEM");

                mailSender.send(mimeMessage);
            } catch (MessagingException e) {
                throw new RuntimeException("Không thể gửi email thông báo với lỗi", e.getCause());
            }
        }
    }

    private String[] exactEmailFromListRecipient(Set<Long> recipients) {
        List<Long> userIds = recipients.stream().toList();
        String[] userEmail = userRepository.findDistinctEmailsById(userIds).toArray(new String[0]);
        return userEmail;
    }
}
