package com.seiki.notificationTest.controller;

import com.seiki.notificationTest.model.Notification;
import com.seiki.notificationTest.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService service;

    @InjectMocks
    private NotificationController controller;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = Notification.builder()
                .id(1L)
                .title("Test")
                .message("Message")
                .type("INFO")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void send_shouldReturn201WhenValidNotification() {
        when(service.send(any(Notification.class))).thenReturn(notification);

        ResponseEntity<Notification> response = controller.send(notification);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test", response.getBody().getTitle());
    }

    @Test
    void findAll_shouldReturn200WithList() {
        when(service.findAll()).thenReturn(Arrays.asList(notification));

        ResponseEntity<List<Notification>> response = controller.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void countUnread_shouldReturn200WithCount() {
        when(service.countUnread()).thenReturn(5L);

        ResponseEntity<Long> response = controller.countUnread();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5L, response.getBody());
    }

    @Test
    void markAsRead_shouldReturn204() {
        doNothing().when(service).markAsRead(1L);

        ResponseEntity<Void> response = controller.markAsRead(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void markAllAsRead_shouldReturn204() {
        doNothing().when(service).markAllAsRead();

        ResponseEntity<Void> response = controller.markAllAsRead();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
