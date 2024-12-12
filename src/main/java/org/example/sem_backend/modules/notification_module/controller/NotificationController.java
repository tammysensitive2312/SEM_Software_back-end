package org.example.sem_backend.modules.notification_module.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.sem_backend.main_service.middleware.auth.service.UserDetailsImpl;
import org.example.sem_backend.modules.notification_module.domain.entity.Notification;
import org.example.sem_backend.modules.notification_module.service.NotificationService;
import org.example.sem_backend.modules.notification_module.service.SseEmitterService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management Controller", description = "APIs for handling real-time notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterService sseEmitterService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "Subscribe to Server-Sent Events (SSE) for real-time notifications",
            description = "Establishes a persistent connection for receiving real-time notifications",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "SSE connection established successfully",
                            content = @Content(mediaType = "text/event-stream")
                    )
            }
    )
    public SseEmitter subscribe(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userInfo
    ) {
        Long userId = userInfo.getId();

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitterService.addEmitter(userId, emitter);

        emitter.onCompletion(() -> sseEmitterService.removeEmitter(userId, emitter));
        emitter.onTimeout(() -> sseEmitterService.removeEmitter(userId, emitter));
        emitter.onError(e -> sseEmitterService.removeEmitter(userId, emitter));

        return emitter;
    }

    @GetMapping("/unread")
    @Operation(
            summary = "Retrieve unread notifications",
            description = "Fetches a list of unread notifications for the authenticated user",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            }
    )
    public List<Notification> getUnreadNotifications(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userInfo
    ) {
        Long userId = userInfo.getId();
        return notificationService.getUnreadNotifications(userId);
    }

    @PostMapping("/{notificationId}/read")
    @Operation(
            summary = "Mark a notification as read",
            description = "Marks a specific notification as read for the user",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            }
    )
    public String markAsRead(
            @Parameter(
                    name = "notificationId",
                    description = "Unique identifier of the notification to mark as read"
            )
            @PathVariable Long notificationId
    ) {
        notificationService.markAsRead(notificationId);
        return "Notification marked as read.";
    }
}