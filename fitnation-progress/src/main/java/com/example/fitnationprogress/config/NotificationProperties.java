package com.example.fitnationprogress.config;

import com.example.fitnationprogress.enums.NotificationEventType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "fitnation.notifications")
public class NotificationProperties {

    private Duration defaultDedupWindow = Duration.ofHours(1);

    private Map<String, String> dedupWindowByEvent = new HashMap<>();

    private int maxNotificationsPerRecipientPerHour = 80;

    private int membershipExpiryLeadDays = 7;

    public Duration resolveDedupWindow(NotificationEventType eventType) {
        String iso = dedupWindowByEvent.get(eventType.name());
        if (iso == null || iso.isBlank()) {
            return defaultDedupWindow;
        }
        return Duration.parse(iso);
    }
}
