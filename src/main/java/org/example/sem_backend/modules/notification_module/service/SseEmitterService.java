package org.example.sem_backend.modules.notification_module.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Manages Server-Sent Event (SSE) emitters for real-time notifications.
 *
 * <p>This service handles the lifecycle and broadcasting of SSE emitters for individual users.
 * It uses thread-safe collections to manage concurrent access to emitters.</p>
 */
@Service
@RequiredArgsConstructor
public class SseEmitterService {
    /**
     * Concurrent map storing SSE emitters for each user.
     *
     * <p>Key features:
     * <ul>
     *   <li>Uses ConcurrentHashMap to ensure thread-safe access to user emitters</li>
     *   <li>Uses CopyOnWriteArraySet to allow safe concurrent modification of emitter sets</li>
     * </ul>
     * </p>
     */
    private final ConcurrentHashMap<Long, CopyOnWriteArraySet<SseEmitter>> emittersMap = new ConcurrentHashMap<>();

    /**
     * Adds a new SSE emitter for a specific user.
     *
     * <p>Uses computeIfAbsent to create a new set of emitters for the user if one doesn't exist,
     * ensuring atomic operation and avoiding potential race conditions.</p>
     *
     * @param userId The ID of the user receiving the emitter
     * @param emitter The SSE emitter to be added
     */
    public void addEmitter(Long userId, SseEmitter emitter) {
        emittersMap.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(emitter);
    }

    /**
     * Removes a specific emitter for a user, cleaning up empty emitter sets.
     *
     * <p>Implements a two-step cleanup:
     * <ol>
     *   <li>Removes the specific emitter from the user's emitter set</li>
     *   <li>Removes the entire user entry if no emitters remain</li>
     * </ol>
     * </p>
     *
     * @param userId The ID of the user owning the emitter
     * @param emitter The SSE emitter to be removed
     */
    public void removeEmitter(Long userId, SseEmitter emitter) {
        CopyOnWriteArraySet<SseEmitter> emitters = emittersMap.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                emittersMap.remove(userId);
            }
        }
    }

    /**
     * Sends a notification to all active emitters for a specific user.
     *
     * <p>Key behaviors:
     * <ul>
     *   <li>Automatically removes failed emitters to prevent memory leaks</li>
     *   <li>Uses a named "notification" event for client-side processing</li>
     *   <li>Silently handles potential IO exceptions during sending</li>
     * </ul>
     * </p>
     *
     * @param userId The ID of the user to receive the notification
     * @param message The notification message to be sent
     */
    public void sendNotification(Long userId, String message) {
        CopyOnWriteArraySet<SseEmitter> emitters = emittersMap.get(userId);
        if (emitters != null) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(message));
                } catch (IOException e) {
                    emitters.remove(emitter);
                }
            }
        }
    }
}