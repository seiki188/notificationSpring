package com.seiki.notificationTest.service;

import com.seiki.notificationTest.model.Notification;
import com.seiki.notificationTest.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        emitters.add(emitter);
        return emitter;
    }

    public Notification send(Notification notification) {
        Notification saved = repository.save(notification);
        broadcast(saved);
        return saved;
    }

    public void markAsRead(Long id) {
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
        notification.setRead(true);
        repository.save(notification);
    }

    public void markAllAsRead() {
        List<Notification> unread = repository.findByReadFalse();
        unread.forEach(n -> n.setRead(true));
        repository.saveAll(unread);
    }

    public List<Notification> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public long countUnread() {
        return repository.countByReadFalse();
    }

    // Envia o evento para todos os clientes SSE conectados
    private void broadcast(Notification notification) {
        List<SseEmitter> dead = new
                CopyOnWriteArrayList<>();

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
            } catch (IOException e) {
                dead.add(emitter);
            }
        });

        emitters.removeAll(dead);
    }
}
