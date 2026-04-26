package com.example.fitnationprogress.service;

import com.example.fitnationprogress.exception.InAppNotificationNotFoundException;
import com.example.fitnationprogress.dto.InAppNotificationResponse;
import com.example.fitnationprogress.mapper.InAppNotificationMapper;
import com.example.fitnationprogress.repository.InAppNotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InAppNotificationQueryService {

    private final InAppNotificationRepository inAppNotificationRepository;
    private final InAppNotificationMapper inAppNotificationMapper;

    public InAppNotificationQueryService(
            InAppNotificationRepository inAppNotificationRepository,
            InAppNotificationMapper inAppNotificationMapper) {
        this.inAppNotificationRepository = inAppNotificationRepository;
        this.inAppNotificationMapper = inAppNotificationMapper;
    }

    @Transactional(readOnly = true)
    public Page<InAppNotificationResponse> listForRecipient(Long recipientUserId, Pageable pageable) {
        return inAppNotificationRepository
                .findByRecipient_IdOrderByCreatedAtDesc(recipientUserId, pageable)
                .map(inAppNotificationMapper::toResponse);
    }

    @Transactional
    public InAppNotificationResponse markRead(Long recipientUserId, Long notificationId) {
        var entity = inAppNotificationRepository
                .findByIdAndRecipient_Id(notificationId, recipientUserId)
                .orElseThrow(InAppNotificationNotFoundException::new);
        entity.markRead();
        return inAppNotificationMapper.toResponse(entity);
    }
}
