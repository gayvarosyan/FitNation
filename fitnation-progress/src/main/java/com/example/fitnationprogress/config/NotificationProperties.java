package com.example.fitnationprogress.config;

import com.example.fitnationprogress.enums.NotificationEventType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "fitnation.notifications")
public class NotificationProperties {

    private Duration defaultDedupWindow = Duration.ofHours(1);

    private Map<String, String> dedupWindowByEvent = new HashMap<>();

    private int maxNotificationsPerRecipientPerHour = 80;

    private int membershipExpiryLeadDays = 7;

    public Duration getDefaultDedupWindow() {
        return defaultDedupWindow;
    }

    public void setDefaultDedupWindow(Duration defaultDedupWindow) {
        this.defaultDedupWindow = defaultDedupWindow;
    }

    public Map<String, String> getDedupWindowByEvent() {
        return dedupWindowByEvent;
    }

    public void setDedupWindowByEvent(Map<String, String> dedupWindowByEvent) {
        this.dedupWindowByEvent = dedupWindowByEvent;
    }

    public int getMaxNotificationsPerRecipientPerHour() {
        return maxNotificationsPerRecipientPerHour;
    }

    public void setMaxNotificationsPerRecipientPerHour(int maxNotificationsPerRecipientPerHour) {
        this.maxNotificationsPerRecipientPerHour = maxNotificationsPerRecipientPerHour;
    }

    public int getMembershipExpiryLeadDays() {
        return membershipExpiryLeadDays;
    }

    public void setMembershipExpiryLeadDays(int membershipExpiryLeadDays) {
        this.membershipExpiryLeadDays = membershipExpiryLeadDays;
    }

    public Duration resolveDedupWindow(NotificationEventType eventType) {
        String iso = dedupWindowByEvent.get(eventType.name());
        if (iso == null || iso.isBlank()) {
            return defaultDedupWindow;
        }
        return Duration.parse(iso);
    }
}
