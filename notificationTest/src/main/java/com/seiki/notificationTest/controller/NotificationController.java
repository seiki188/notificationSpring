package com.seiki.notificationTest.controller;

import com.seiki.notificationTest.model.Notification;
import com.seiki.notificationTest.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Notifications", description = "Real-time notification management")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to notifications", description = "Opens SSE connection for real-time notifications")
    public SseEmitter stream() {
        return service.subscribe();
    }

    @PostMapping
    @Operation(summary = "Send notification", description = "Creates and broadcasts a new notification")
    public ResponseEntity<Notification> send(@Valid @RequestBody Notification notification) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.send(notification));
    }

    @GetMapping
    @Operation(summary = "Get all notifications", description = "Returns all notifications ordered by creation date")
    public ResponseEntity<List<Notification>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Count unread notifications", description = "Returns the count of unread notifications")
    public ResponseEntity<Long> countUnread() {
        return ResponseEntity.ok(service.countUnread());
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark as read", description = "Marks a specific notification as read")
    public ResponseEntity<Void> markAsRead(@Parameter(description = "Notification ID") @PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all as read", description = "Marks all notifications as read")
    public ResponseEntity<Void> markAllAsRead() {
        service.markAllAsRead();
        return ResponseEntity.noContent().build();
    }
}