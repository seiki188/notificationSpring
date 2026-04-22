package com.seiki.notificationTest.controller;

import com.seiki.notificationTest.model.Notification;
import com.seiki.notificationTest.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Validated
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return service.subscribe();
    }

    @PostMapping
    public ResponseEntity<Notification> send(@Valid @RequestBody Notification notification) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.send(notification));
    }

    @GetMapping
    public ResponseEntity<List<Notification>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnread() {
        return ResponseEntity.ok(service.countUnread());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        service.markAllAsRead();
        return ResponseEntity.noContent().build();
    }
}