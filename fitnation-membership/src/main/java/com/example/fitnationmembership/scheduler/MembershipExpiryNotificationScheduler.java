package com.example.fitnationmembership.scheduler;

import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationmembership.model.Membership;
import com.example.fitnationmembership.repository.MembershipRepository;
import com.example.fitnationprogress.config.NotificationProperties;
import com.example.fitnationprogress.factory.NotificationCommandFactory;
import com.example.fitnationprogress.service.NotificationCommandPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class MembershipExpiryNotificationScheduler {

    private final MembershipRepository membershipRepository;
    private final NotificationCommandPublisher notificationCommandPublisher;
    private final NotificationProperties notificationProperties;

    public MembershipExpiryNotificationScheduler(
            MembershipRepository membershipRepository,
            NotificationCommandPublisher notificationCommandPublisher,
            NotificationProperties notificationProperties) {
        this.membershipRepository = membershipRepository;
        this.notificationCommandPublisher = notificationCommandPublisher;
        this.notificationProperties = notificationProperties;
    }

    @Scheduled(cron = "${fitnation.notifications.membership-expiry-cron:0 15 7 * * ?}")
    public void publishMembershipExpiryWarnings() {
        int leadDays = notificationProperties.getMembershipExpiryLeadDays();
        LocalDate expiryDate = LocalDate.now().plusDays(leadDays);
        List<Membership> memberships = membershipRepository.findAllByStatusAndEndDate(MembershipStatus.ACTIVE, expiryDate);
        for (Membership membership : memberships) {
            notificationCommandPublisher.publishAfterCommit(
                    NotificationCommandFactory.membershipExpiringSoon(
                            membership.getId(),
                            membership.getUser().getId(),
                            membership.getMembershipType().getName(),
                            leadDays));
        }
    }
}
