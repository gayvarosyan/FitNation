package com.example.fitnationuser.device;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.enums.DeviceSessionStatus;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.exception.QrSessionAlreadyUsedException;
import com.example.fitnationcommon.exception.QrSessionExpiredException;
import com.example.fitnationcommon.exception.QrSessionInvalidException;
import com.example.fitnationcommon.exception.RateLimitExceededException;
import com.example.fitnationuser.device.service.impl.DeviceLoginSessionServiceImpl;
import com.example.fitnationuser.security.JwtService;
import com.example.fitnationuser.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceLoginSessionServiceTest {

    @InjectMocks
    DeviceLoginSessionServiceImpl service;

    @Mock
    DeviceLoginSessionRepository sessionRepository;

    @Mock
    JwtService jwtService;

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
    }

    String buildPayload(String sessionId, String rawSecret) {
        var encoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(rawSecret.getBytes(StandardCharsets.UTF_8));
        return sessionId + ":" + encoded;
    }

    String hashSecret(String raw) throws Exception {
        var digest = MessageDigest.getInstance("SHA-256");
        var hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    DeviceLoginSession pendingSession(String rawSecret, LocalDateTime expiresAt) throws Exception {
        var s = new DeviceLoginSession();
        s.setId("session-id");
        s.setInitiatorUser(user);
        s.setStatus(DeviceSessionStatus.PENDING);
        s.setSecretHash(hashSecret(rawSecret));
        s.setExpiresAt(expiresAt);
        s.setCreatedAt(LocalDateTime.now());
        return s;
    }

    @Test
    void createSession_happyPath_sessionSavedAndResponseReturned() {
        when(sessionRepository.countByInitiatorUserIdAndCreatedAtAfter(eq(1L), any()))
                .thenReturn(0L);

        var response = service.createSession(user);

        verify(sessionRepository).save(any(DeviceLoginSession.class));
        assertThat(response.getSessionId()).isNotNull();
        assertThat(response.getQrPayload()).contains(":");
        assertThat(response.getExpiresAt()).isAfter(LocalDateTime.now());
    }

    @Test
    void createSession_rateLimitNotReached_succeeds() {
        when(sessionRepository.countByInitiatorUserIdAndCreatedAtAfter(eq(1L), any()))
                .thenReturn(4L);

        service.createSession(user);

        verify(sessionRepository).save(any());
    }

    @Test
    void createSession_rateLimitReached_throwsRateLimitExceededException() {
        when(sessionRepository.countByInitiatorUserIdAndCreatedAtAfter(eq(1L), any()))
                .thenReturn(5L);

        assertThatThrownBy(() -> service.createSession(user))
                .isInstanceOf(RateLimitExceededException.class);

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void redeemSession_happyPath_returnsJwtAndMarksConsumed() throws Exception {
        var rawSecret = "valid-secret";
        var session = pendingSession(rawSecret, LocalDateTime.now().plusMinutes(5));

        when(sessionRepository.findById("session-id")).thenReturn(Optional.of(session));
        when(jwtService.generateAccessToken(user)).thenReturn("jwt-token");

        var token = service.redeemSession(buildPayload("session-id", rawSecret));

        assertThat(token).isEqualTo("jwt-token");
        verify(sessionRepository).save(argThat(s ->
                s.getStatus() == DeviceSessionStatus.CONSUMED &&
                        s.getConsumedAt() != null
        ));
    }

    @Test
    void redeemSession_noColon_throwsQrSessionInvalidException() {
        assertThatThrownBy(() -> service.redeemSession("nocolonpayload"))
                .isInstanceOf(QrSessionInvalidException.class)
                .hasMessageContaining(ApplicationConstants.QR_INVALID_FORMAT);
    }

    @Test
    void redeemSession_invalidBase64_throwsQrSessionInvalidException() {
        assertThatThrownBy(() -> service.redeemSession("session-id:!!!invalid!!!"))
                .isInstanceOf(QrSessionInvalidException.class)
                .hasMessageContaining(ApplicationConstants.QR_INVALID_ENCODING);
    }

    @Test
    void redeemSession_sessionNotFound_throwsQrSessionInvalidException() {
        when(sessionRepository.findById("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.redeemSession(buildPayload("ghost", "secret")))
                .isInstanceOf(QrSessionInvalidException.class)
                .hasMessageContaining(ApplicationConstants.QR_SESSION_NOT_FOUND);
    }

    @Test
    void redeemSession_alreadyConsumed_throwsQrSessionAlreadyUsedException() throws Exception {
        var session = pendingSession("secret", LocalDateTime.now().plusMinutes(5));
        session.setStatus(DeviceSessionStatus.CONSUMED);

        when(sessionRepository.findById("session-id")).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> service.redeemSession(buildPayload("session-id", "secret")))
                .isInstanceOf(QrSessionAlreadyUsedException.class);

        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void redeemSession_revoked_throwsQrSessionInvalidException() throws Exception {
        var session = pendingSession("secret", LocalDateTime.now().plusMinutes(5));
        session.setStatus(DeviceSessionStatus.REVOKED);

        when(sessionRepository.findById("session-id")).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> service.redeemSession(buildPayload("session-id", "secret")))
                .isInstanceOf(QrSessionInvalidException.class)
                .hasMessageContaining(ApplicationConstants.QR_REVOKED);
    }

    @Test
    void redeemSession_expired_throwsQrSessionExpiredExceptionAndSavesExpiredStatus() throws Exception {
        var session = pendingSession("secret", LocalDateTime.now().minusSeconds(1));

        when(sessionRepository.findById("session-id")).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> service.redeemSession(buildPayload("session-id", "secret")))
                .isInstanceOf(QrSessionExpiredException.class);

        verify(sessionRepository).save(argThat(s -> s.getStatus() == DeviceSessionStatus.EXPIRED));
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void redeemSession_wrongSecret_throwsQrSessionInvalidException() throws Exception {
        var session = pendingSession("correct-secret", LocalDateTime.now().plusMinutes(5));

        when(sessionRepository.findById("session-id")).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> service.redeemSession(buildPayload("session-id", "wrong-secret")))
                .isInstanceOf(QrSessionInvalidException.class)
                .hasMessageContaining(ApplicationConstants.QR_INVALID_SECRET);

        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void getSessionStatus_pending_returnsPending() throws Exception {
        var session = pendingSession("secret", LocalDateTime.now().plusMinutes(5));

        when(sessionRepository.findById("session-id")).thenReturn(Optional.of(session));

        var status = service.getSessionStatus("session-id", user);

        assertThat(status).isEqualTo(DeviceSessionStatus.PENDING);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void getSessionStatus_pendingButExpired_returnsExpiredAndSaves() throws Exception {
        var session = pendingSession("secret", LocalDateTime.now().minusSeconds(1));

        when(sessionRepository.findById("session-id")).thenReturn(Optional.of(session));

        var status = service.getSessionStatus("session-id", user);

        assertThat(status).isEqualTo(DeviceSessionStatus.EXPIRED);
        verify(sessionRepository).save(argThat(s -> s.getStatus() == DeviceSessionStatus.EXPIRED));
    }

    @Test
    void getSessionStatus_consumed_returnsConsumed() throws Exception {
        var session = pendingSession("secret", LocalDateTime.now().plusMinutes(5));
        session.setStatus(DeviceSessionStatus.CONSUMED);

        when(sessionRepository.findById("session-id")).thenReturn(Optional.of(session));

        var status = service.getSessionStatus("session-id", user);

        assertThat(status).isEqualTo(DeviceSessionStatus.CONSUMED);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void getSessionStatus_sessionNotFound_throwsQrSessionInvalidException() {
        when(sessionRepository.findById("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getSessionStatus("ghost", user))
                .isInstanceOf(QrSessionInvalidException.class);
    }

    @Test
    void getSessionStatus_wrongOwner_throwsForbiddenOperationException() throws Exception {
        var stranger = new User();
        stranger.setId(99L);

        var session = pendingSession("secret", LocalDateTime.now().plusMinutes(5));

        when(sessionRepository.findById("session-id")).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> service.getSessionStatus("session-id", stranger))
                .isInstanceOf(ForbiddenOperationException.class);
    }
}