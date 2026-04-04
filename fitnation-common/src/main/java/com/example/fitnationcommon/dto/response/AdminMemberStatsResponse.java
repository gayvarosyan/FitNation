package com.example.fitnationcommon.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminMemberStatsResponse {

    private long totalActiveUsers;
    private long usersWithActiveSubscription;
    private double premiumTierPercent;
    private long totalMembers;
    private long blockedMembers;

    public static AdminMemberStatsResponse empty() {
        return AdminMemberStatsResponse.builder()
                .totalActiveUsers(0)
                .usersWithActiveSubscription(0)
                .premiumTierPercent(0.0)
                .totalMembers(0)
                .blockedMembers(0)
                .build();
    }
}
