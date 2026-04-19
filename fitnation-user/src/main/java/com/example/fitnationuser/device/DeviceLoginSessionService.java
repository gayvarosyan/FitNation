package com.example.fitnationuser.device;

import com.example.fitnationcommon.dto.response.CreateQrSessionResponse;
import com.example.fitnationcommon.enums.DeviceSessionStatus;
import com.example.fitnationuser.user.User;

public interface DeviceLoginSessionService {

    CreateQrSessionResponse createSession(User initiator);

    String redeemSession(String qrPayload);

    DeviceSessionStatus getSessionStatus(String sessionId, User requester);
}
