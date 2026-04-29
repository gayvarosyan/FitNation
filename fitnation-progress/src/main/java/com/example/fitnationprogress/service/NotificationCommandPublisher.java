package com.example.fitnationprogress.service;

import com.example.fitnationprogress.dto.NotificationTriggerCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@RequiredArgsConstructor
public class NotificationCommandPublisher {

    private final NotificationTriggerService notificationTriggerService;

    public void publishAfterCommit(NotificationTriggerCommand command) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    notificationTriggerService.dispatch(command);
                }
            });
        } else {
            notificationTriggerService.dispatch(command);
        }
    }
}
