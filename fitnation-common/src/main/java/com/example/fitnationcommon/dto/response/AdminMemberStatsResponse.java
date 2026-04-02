package com.example.fitnationcommon.dto.response;

import lombok.Data;

@Data
public class AdminMemberStatsResponse {

    private long totalActiveUsers;
    private long usersWithActiveSubscription;
    private double premiumTierPercent;
    private long totalMembers;
    private long blockedMembers;
    
    public static AdminMemberStatsResponse empty() {
        AdminMemberStatsResponse response = new AdminMemberStatsResponse();
        response.setTotalActiveUsers(0);
        response.setUsersWithActiveSubscription(0);
        response.setPremiumTierPercent(0.0);
        response.setTotalMembers(0);
        response.setBlockedMembers(0);
        return response;
    }
}
