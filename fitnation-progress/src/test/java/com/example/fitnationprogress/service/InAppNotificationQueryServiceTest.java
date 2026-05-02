package com.example.fitnationprogress.service;

import com.example.fitnationprogress.dto.InAppNotificationResponse;
import com.example.fitnationprogress.enums.NotificationEntityType;
import com.example.fitnationprogress.enums.NotificationEventType;
import com.example.fitnationprogress.enums.NotificationSeverity;
import com.example.fitnationprogress.exception.InAppNotificationNotFoundException;
import com.example.fitnationprogress.mapper.InAppNotificationMapper;
import com.example.fitnationprogress.model.InAppNotification;
import com.example.fitnationprogress.repository.InAppNotificationRepository;
import com.example.fitnationuser.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InAppNotificationQueryServiceTest {

    @Mock
    private InAppNotificationRepository inAppNotificationRepository;
    @Mock
    private InAppNotificationMapper inAppNotificationMapper;

    @InjectMocks
    private InAppNotificationQueryService inAppNotificationQueryService;

    @Test
    void listForRecipientMapsPage() {
        User recipient = mock(User.class);
        InAppNotification row = sampleNotification(recipient);
        when(inAppNotificationRepository.findByRecipient_IdOrderByCreatedAtDesc(eq(7L), any()))
                .thenReturn(new PageImpl<>(List.of(row)));
        when(inAppNotificationMapper.toResponse(row)).thenReturn(sampleResponse());

        var page = inAppNotificationQueryService.listForRecipient(7L, PageRequest.of(0, 20));
        assertEquals(1, page.getTotalElements());
        assertEquals("Hello", page.getContent().getFirst().title());
    }

    @Test
    void markReadUpdatesEntity() {
        User recipient = mock(User.class);
        InAppNotification row = sampleNotification(recipient);
        when(inAppNotificationRepository.findByIdAndRecipient_Id(3L, 7L)).thenReturn(Optional.of(row));
        when(inAppNotificationMapper.toResponse(row)).thenReturn(sampleResponse());

        InAppNotificationResponse response = inAppNotificationQueryService.markRead(7L, 3L);
        assertEquals("Hello", response.title());
        assertTrue(row.isRead());
        verify(inAppNotificationMapper).toResponse(row);
    }

    @Test
    void markReadMissingThrows() {
        when(inAppNotificationRepository.findByIdAndRecipient_Id(3L, 7L)).thenReturn(Optional.empty());
        assertThrows(InAppNotificationNotFoundException.class, () -> inAppNotificationQueryService.markRead(7L, 3L));
    }

    private static InAppNotification sampleNotification(User recipient) {
        return InAppNotification.builder()
                .id(3L)
                .recipient(recipient)
                .eventType(NotificationEventType.CLASS_BOOKED)
                .title("Hello")
                .body("World")
                .read(false)
                .createdAt(Instant.parse("2026-04-26T10:00:00Z"))
                .metadataJson("{}")
                .severity(NotificationSeverity.INFO)
                .entityType(NotificationEntityType.CLASS_BOOKING)
                .entityId(9L)
                .build();
    }

    private static InAppNotificationResponse sampleResponse() {
        return new InAppNotificationResponse(
                3L,
                7L,
                NotificationEventType.CLASS_BOOKED,
                "Hello",
                "World",
                false,
                Instant.parse("2026-04-26T10:00:00Z"),
                "{}",
                NotificationSeverity.INFO);
    }

}
