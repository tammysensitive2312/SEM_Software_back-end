package org.example.sem_backend.modules.notification_module.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@RequiredArgsConstructor
public class SseEmitterService {
    private final ConcurrentHashMap<Long, CopyOnWriteArraySet<SseEmitter>> emittersMap = new ConcurrentHashMap<>();

    public void addEmitter(Long userId, SseEmitter emitter) {
        emittersMap.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(emitter);
    }

    public void removeEmitter(Long userId, SseEmitter emitter) {
        CopyOnWriteArraySet<SseEmitter> emitters = emittersMap.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                emittersMap.remove(userId);
            }
        }
    }

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
