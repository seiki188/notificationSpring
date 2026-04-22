package com.seiki.notificationTest.service;

import com.seiki.notificationTest.model.Notification;
import com.seiki.notificationTest.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationService service;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = Notification.builder()
                .id(1L)
                .title("Test Title")
                .message("Test Message")
                .type("INFO")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void send_shouldSaveAndReturnNotification() {
        when(repository.save(any(Notification.class))).thenReturn(notification);

        Notification result = service.send(notification);

        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        verify(repository, times(1)).save(notification);
    }

    @Test
    void markAsRead_shouldUpdateReadStatus() {
        when(repository.findById(1L)).thenReturn(Optional.of(notification));
        when(repository.save(any(Notification.class))).thenReturn(notification);

        service.markAsRead(1L);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(notification);
        assertTrue(notification.getRead());
    }

    @Test
    void markAsRead_shouldThrowExceptionWhenNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.markAsRead(999L));
    }

    @Test
    void findAll_shouldReturnAllNotificationsOrderedByCreatedAt() {
        List<Notification> notifications = Arrays.asList(notification);
        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(notifications);

        List<Notification> result = service.findAll();

        assertEquals(1, result.size());
        verify(repository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void countUnread_shouldReturnCount() {
        when(repository.countByReadFalse()).thenReturn(5L);

        long count = service.countUnread();

        assertEquals(5L, count);
        verify(repository, times(1)).countByReadFalse();
    }

    @Test
    void markAllAsRead_shouldUpdateAllUnread() {
        List<Notification> unreadList = Arrays.asList(notification);
        when(repository.findByReadFalse()).thenReturn(unreadList);

        service.markAllAsRead();

        verify(repository, times(1)).findByReadFalse();
        verify(repository, times(1)).saveAll(unreadList);
        assertTrue(notification.getRead());
    }
}
