package com.example.fitnationuser.device.service.impl;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.response.CreateQrSessionResponse;
import com.example.fitnationcommon.enums.DeviceSessionStatus;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.exception.QrSessionAlreadyUsedException;
import com.example.fitnationcommon.exception.QrSessionExpiredException;
import com.example.fitnationcommon.exception.QrSessionInvalidException;
import com.example.fitnationcommon.exception.RateLimitExceededException;
import com.example.fitnationuser.device.DeviceLoginSession;
import com.example.fitnationuser.device.DeviceLoginSessionRepository;
import com.example.fitnationuser.device.DeviceLoginSessionService;
import com.example.fitnationuser.security.JwtService;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceLoginSessionServiceImpl implements DeviceLoginSessionService {

    private final DeviceLoginSessionRepository sessionRepository;
    private final JwtService jwtService;

    @Override
    public CreateQrSessionResponse createSession(User initiator) {
        enforceRateLimit(initiator.getId());

        var rawSecret = generateSecret();
        var sessionId = UUID.randomUUID().toString();

        var session = DeviceLoginSession.builder()
                .id(sessionId)
                .initiatorUser(initiator)
                .status(DeviceSessionStatus.PENDING)
                .secretHash(hashSecret(rawSecret))
                .expiresAt(LocalDateTime.now().plusMinutes(ApplicationConstants.QR_SESSION_TTL_MINUTES))
                .build();

        sessionRepository.save(session);

        log.info("QR session created: sessionId={}, userId={}", sessionId, initiator.getId());

        var qrPayload = sessionId + ":" + Base64.getUrlEncoder().withoutPadding()
                .encodeToString(rawSecret.getBytes(StandardCharsets.UTF_8));

        return CreateQrSessionResponse.builder()
                .sessionId(sessionId)
                .qrPayload(qrPayload)
                .expiresAt(session.getExpiresAt())
                .build();
    }

    @Override
    @Transactional
    public String redeemSession(String qrPayload) {
        var parts = parseQrPayload(qrPayload);
        var sessionId = parts[0];
        var rawSecret = decodeSecret(parts[1]);

        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new QrSessionInvalidException(ApplicationConstants.QR_SESSION_NOT_FOUND));

        validateSessionState(session);
        validateSecret(rawSecret, session);
        consumeSession(session);

        log.info("QR session redeemed: sessionId={}, userId={}",
                sessionId, session.getInitiatorUser().getId());

        return jwtService.generateAccessToken(session.getInitiatorUser());
    }

    @Override
    public DeviceSessionStatus getSessionStatus(String sessionId, User requester) {
        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new QrSessionInvalidException(ApplicationConstants.QR_SESSION_NOT_FOUND));

        if (!session.getInitiatorUser().getId().equals(requester.getId())) {
            throw new ForbiddenOperationException(ApplicationConstants.QR_NOT_YOUR_SESSION);
        }

        if (session.getStatus() == DeviceSessionStatus.PENDING
                && LocalDateTime.now().isAfter(session.getExpiresAt())) {
            session.setStatus(DeviceSessionStatus.EXPIRED);
            sessionRepository.save(session);
        }

        return session.getStatus();
    }


    private String[] parseQrPayload(String qrPayload) {
        var parts = qrPayload.split(":");
        if (parts.length != 2) {
            throw new QrSessionInvalidException(ApplicationConstants.QR_INVALID_FORMAT);
        }
        return parts;
    }

    private String decodeSecret(String encoded) {
        try {
            return new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new QrSessionInvalidException(ApplicationConstants.QR_INVALID_ENCODING);
        }
    }

    private void validateSessionState(DeviceLoginSession session) {
        if (session.getStatus() == DeviceSessionStatus.CONSUMED) {
            throw new QrSessionAlreadyUsedException(ApplicationConstants.QR_ALREADY_USED);
        }
        if (session.getStatus() == DeviceSessionStatus.REVOKED) {
            throw new QrSessionInvalidException(ApplicationConstants.QR_REVOKED);
        }
        if (LocalDateTime.now().isAfter(session.getExpiresAt())) {
            session.setStatus(DeviceSessionStatus.EXPIRED);
            sessionRepository.save(session);
            throw new QrSessionExpiredException(ApplicationConstants.QR_EXPIRED);
        }
    }

    private void validateSecret(String rawSecret, DeviceLoginSession session) {
        if (!hashSecret(rawSecret).equals(session.getSecretHash())) {
            throw new QrSessionInvalidException(ApplicationConstants.QR_INVALID_SECRET);
        }
    }

    private void consumeSession(DeviceLoginSession session) {
        session.setStatus(DeviceSessionStatus.CONSUMED);
        session.setConsumedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    private void enforceRateLimit(Long userId) {
        var windowStart = LocalDateTime.now().minusMinutes(ApplicationConstants.QR_RATE_LIMIT_WINDOW_MINUTES);
        var count = sessionRepository.countByInitiatorUserIdAndCreatedAtAfter(userId, windowStart);
        if (count >= ApplicationConstants.QR_MAX_SESSIONS_PER_WINDOW) {
            throw new RateLimitExceededException(ApplicationConstants.QR_RATE_LIMIT_EXCEEDED);
        }
    }

    private String generateSecret() {
        var bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashSecret(String raw) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}