package com.example.fitnationuser.device;


import com.example.fitnationcommon.dto.response.CreateQrSessionResponse;
import com.example.fitnationcommon.enums.DeviceSessionStatus;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.exception.QrSessionAlreadyUsedException;
import com.example.fitnationcommon.exception.QrSessionExpiredException;
import com.example.fitnationcommon.exception.QrSessionInvalidException;
import com.example.fitnationcommon.exception.RateLimitExceededException;
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
public class DeviceLoginSessionService {

    private static final int SESSION_TTL_MINUTES = 5;
    private static final int MAX_SESSIONS_PER_WINDOW = 5;
    private static final int RATE_LIMIT_WINDOW_MINUTES = 10;

    private final DeviceLoginSessionRepository sessionRepository;
    private final JwtService jwtService;

    public CreateQrSessionResponse createSession(User initiator) {
        enforceRateLimit(initiator.getId());

        String rawSecret = generateSecret();
        String sessionId = UUID.randomUUID().toString();

        DeviceLoginSession session = DeviceLoginSession.builder()
                .id(sessionId)
                .initiatorUser(initiator)
                .status(DeviceSessionStatus.PENDING)
                .secretHash(hashSecret(rawSecret))
                .expiresAt(LocalDateTime.now().plusMinutes(SESSION_TTL_MINUTES))
                .build();

        sessionRepository.save(session);

        log.info("QR session created: sessionId={}, userId={}", sessionId, initiator.getId());

        String qrPayload = sessionId + ":" + Base64.getUrlEncoder().withoutPadding()
                .encodeToString(rawSecret.getBytes(StandardCharsets.UTF_8));

        return CreateQrSessionResponse.builder()
                .sessionId(sessionId)
                .qrPayload(qrPayload)
                .expiresAt(session.getExpiresAt())
                .build();
    }

    @Transactional
    public String redeemSession(String qrPayload) {
        String[] parts = qrPayload.split(":");
        if (parts.length != 2) {
            throw new QrSessionInvalidException("Invalid QR payload format");
        }

        String sessionId = parts[0];
        String rawSecret;
        try {
            rawSecret = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );
        } catch (IllegalArgumentException e) {
            throw new QrSessionInvalidException("Invalid QR payload encoding");
        }

        DeviceLoginSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new QrSessionInvalidException("Session not found"));

        if (session.getStatus() == DeviceSessionStatus.CONSUMED) {
            throw new QrSessionAlreadyUsedException("QR code has already been used");
        }

        if (session.getStatus() == DeviceSessionStatus.REVOKED) {
            throw new QrSessionInvalidException("Session has been revoked");
        }

        if (LocalDateTime.now().isAfter(session.getExpiresAt())) {
            session.setStatus(DeviceSessionStatus.EXPIRED);
            sessionRepository.save(session);
            throw new QrSessionExpiredException("QR code has expired");
        }

        if (!hashSecret(rawSecret).equals(session.getSecretHash())) {
            throw new QrSessionInvalidException("Invalid QR secret");
        }

        session.setStatus(DeviceSessionStatus.CONSUMED);
        session.setConsumedAt(LocalDateTime.now());
        sessionRepository.save(session);

        log.info("QR session redeemed: sessionId={}, userId={}",
                sessionId, session.getInitiatorUser().getId());

        return jwtService.generateAccessToken(session.getInitiatorUser());
    }

    public DeviceSessionStatus getSessionStatus(String sessionId, User requester) {
        DeviceLoginSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new QrSessionInvalidException("Session not found"));

        if (!session.getInitiatorUser().getId().equals(requester.getId())) {
            throw new ForbiddenOperationException("Not your session");
        }

        if (session.getStatus() == DeviceSessionStatus.PENDING
                && LocalDateTime.now().isAfter(session.getExpiresAt())) {
            session.setStatus(DeviceSessionStatus.EXPIRED);
            sessionRepository.save(session);
        }

        return session.getStatus();
    }

    private void enforceRateLimit(Long userId) {
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(RATE_LIMIT_WINDOW_MINUTES);
        long count = sessionRepository.countByInitiatorUserIdAndCreatedAtAfter(userId, windowStart);
        if (count >= MAX_SESSIONS_PER_WINDOW) {
            throw new RateLimitExceededException("Too many QR sessions created. Try again later.");
        }
    }

    private String generateSecret() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashSecret(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}