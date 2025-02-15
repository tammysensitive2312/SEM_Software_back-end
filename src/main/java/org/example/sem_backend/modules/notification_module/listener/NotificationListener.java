package org.example.sem_backend.modules.notification_module.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.common.event.EquipmentBorrowedEvent;
import org.example.sem_backend.common_module.common.event.EquipmentRequestDeniedEvent;
import org.example.sem_backend.common_module.common.event.GenericEvent;
import org.example.sem_backend.modules.notification_module.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
    private final NotificationService notificationService;

    // Map để theo dõi số lượng thông báo bị quá tải của từng người dùng
    private final Map<Long, Integer> overloadedNotifications = new HashMap<>();
    private static final int OVERLOAD_THRESHOLD = 5;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @EventListener
    public void handleEquipmentBorrowedEvent(EquipmentBorrowedEvent event) {
        Long userId = event.getUserId();
        Long requestId = event.getRequestId();
        String message = "Đơn mượn #" + requestId + " của bạn đã được duyệt thành công.";

        log.info("Handling EquipmentBorrowedEvent for userId: {}", userId);
        notificationService.createAndSendNotification(userId, message, true);
    }

    @EventListener
    public void handleRoomStatusChangedEvent(GenericEvent<List<Long>> event) {
        List<Long> userIds = event.getData();
        String message = "Phòng hiện đang gặp sự cố, admin đang xử lý";

        log.info("Handling RoomStatusChangedEvent for userIds: {}", userIds);
        notificationService.createAndSendNotification(userIds, message, true);
    }

    @EventListener
    public void handleEquipmentRequestDeniedEvent(EquipmentRequestDeniedEvent event) {
        Long userId = event.getUserId();
        Long requestId = event.getRequestId();
        String message = "Đơn mượn #" + requestId + " của bạn đã bị từ chối với lý do: " + event.getReason();

        log.info("Handling EquipmentRequestDeniedEvent for userId: {}", userId);
        notificationService.createAndSendNotification(userId, message, true);
    }

    /**
     * Lắng nghe thông báo bị quá tải từ Kafka.
     */
    @KafkaListener(topics = "notification_topic", groupId = "notification_group")
    public void handleOverloadedNotifications(String message) {
        // Parse message để lấy userId và thông tin
        Long userId = extractUserIdFromMessage(message);

        // Cập nhật số lượng thông báo bị quá tải cho userId
        overloadedNotifications.put(userId, overloadedNotifications.getOrDefault(userId, 0) + 1);

        log.info("UserId {} has {} overloaded notifications", userId, overloadedNotifications.get(userId));

        // Nếu đạt ngưỡng, gửi thông báo tổng hợp và reset đếm
        if (overloadedNotifications.get(userId) >= OVERLOAD_THRESHOLD) {
            sendAggregateNotification(userId);
            overloadedNotifications.put(userId, 0);
        }
    }

    /**
     * Gửi thông báo tổng hợp cho người dùng.
     */
    private void sendAggregateNotification(Long userId) {
        String aggregateMessage = "Bạn có 5 thông báo mới.";
        log.info("Sending aggregate notification to userId: {}", userId);

        notificationService.createAndSendNotification(userId, aggregateMessage, false);
    }

    /**
     * Giả lập hàm parse message từ Kafka để lấy userId.
     */
    private Long extractUserIdFromMessage(String message) {
        // Giả sử message có dạng JSON {"userId":123, "message":"..."}
        try {
            Map<String, Object> data = OBJECT_MAPPER.readValue(message, Map.class);
            return Long.parseLong(data.get("userId").toString());
        } catch (Exception e) {
            log.error("Failed to extract userId from message: {}", message, e);
            return null;
        }
    }
}
