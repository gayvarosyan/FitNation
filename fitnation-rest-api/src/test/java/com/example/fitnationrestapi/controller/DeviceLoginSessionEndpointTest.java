package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.request.RedeemQrRequest;
import com.example.fitnationcommon.dto.response.CreateQrSessionResponse;
import com.example.fitnationcommon.enums.DeviceSessionStatus;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.exception.QrSessionAlreadyUsedException;
import com.example.fitnationcommon.exception.QrSessionExpiredException;
import com.example.fitnationcommon.exception.QrSessionInvalidException;
import com.example.fitnationcommon.exception.RateLimitExceededException;
import com.example.fitnationrestapi.endpoint.DeviceLoginSessionEndpoint;
import com.example.fitnationrestapi.exception.GlobalExceptionHandler;
import com.example.fitnationuser.device.service.DeviceLoginSessionService;
import com.example.fitnationuser.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DeviceLoginSessionEndpointTest {

    @Mock
    private DeviceLoginSessionService sessionService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private User user;

    private static final Filter deviceSessionApiAuthFilter = (request, response, chain) -> {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("POST".equalsIgnoreCase(httpRequest.getMethod())
                && httpRequest.getRequestURI().endsWith("/login/qr")) {
            chain.doFilter(request, response);
            return;
        }
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        chain.doFilter(request, response);
    };

    private static final class AuthenticationPrincipalFromSecurityContextResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
        }

        @Override
        public Object resolveArgument(
                MethodParameter parameter,
                ModelAndViewContainer mavContainer,
                NativeWebRequest webRequest,
                WebDataBinderFactory binderFactory) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null ? auth.getPrincipal() : null;
        }
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new DeviceLoginSessionEndpoint(sessionService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .setCustomArgumentResolvers(new AuthenticationPrincipalFromSecurityContextResolver())
                .addFilters(deviceSessionApiAuthFilter)
                .build();

        user = new User();
        user.setId(1L);

        var auth = new UsernamePasswordAuthenticationToken(user, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createSession_authenticated_returns200WithQrResponse() throws Exception {
        var response = CreateQrSessionResponse.builder()
                .sessionId("session-id")
                .qrPayload("session-id:encoded-secret")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        when(sessionService.createSession(any(User.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/devices/qr-session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session-id"))
                .andExpect(jsonPath("$.qrPayload").value("session-id:encoded-secret"))
                .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    void createSession_rateLimitExceeded_returns429() throws Exception {
        when(sessionService.createSession(any(User.class)))
                .thenThrow(new RateLimitExceededException(ApplicationConstants.QR_RATE_LIMIT_EXCEEDED));

        mockMvc.perform(post("/api/auth/devices/qr-session"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void createSession_unauthenticated_returns401() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(post("/api/auth/devices/qr-session"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void redeemSession_validPayload_returns200WithAccessToken() throws Exception {
        var request = new RedeemQrRequest();
        request.setQrPayload("session-id:encoded-secret");

        when(sessionService.redeemSession("session-id:encoded-secret"))
                .thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }

    @Test
    void redeemSession_invalidPayloadFormat_returns400() throws Exception {
        var request = new RedeemQrRequest();
        request.setQrPayload("invalid-no-colon");

        when(sessionService.redeemSession("invalid-no-colon"))
                .thenThrow(new QrSessionInvalidException(ApplicationConstants.QR_INVALID_FORMAT));

        mockMvc.perform(post("/api/auth/login/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void redeemSession_alreadyConsumed_returns409() throws Exception {
        var request = new RedeemQrRequest();
        request.setQrPayload("session-id:encoded-secret");

        when(sessionService.redeemSession(any()))
                .thenThrow(new QrSessionAlreadyUsedException(ApplicationConstants.QR_ALREADY_USED));

        mockMvc.perform(post("/api/auth/login/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void redeemSession_expired_returns410() throws Exception {
        var request = new RedeemQrRequest();
        request.setQrPayload("session-id:encoded-secret");

        when(sessionService.redeemSession(any()))
                .thenThrow(new QrSessionExpiredException(ApplicationConstants.QR_EXPIRED));

        mockMvc.perform(post("/api/auth/login/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isGone());
    }

    @Test
    void redeemSession_missingBody_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/login/qr")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSessionStatus_pendingSession_returns200WithStatus() throws Exception {
        when(sessionService.getSessionStatus(eq("session-id"), any(User.class)))
                .thenReturn(DeviceSessionStatus.PENDING);

        mockMvc.perform(get("/api/auth/devices/qr-session/session-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getSessionStatus_consumedSession_returns200WithConsumedStatus() throws Exception {
        when(sessionService.getSessionStatus(eq("session-id"), any(User.class)))
                .thenReturn(DeviceSessionStatus.CONSUMED);

        mockMvc.perform(get("/api/auth/devices/qr-session/session-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONSUMED"));
    }

    @Test
    void getSessionStatus_expiredSession_returns200WithExpiredStatus() throws Exception {
        when(sessionService.getSessionStatus(eq("session-id"), any(User.class)))
                .thenReturn(DeviceSessionStatus.EXPIRED);

        mockMvc.perform(get("/api/auth/devices/qr-session/session-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EXPIRED"));
    }

    @Test
    void getSessionStatus_sessionNotFound_returns400() throws Exception {
        when(sessionService.getSessionStatus(eq("ghost"), any(User.class)))
                .thenThrow(new QrSessionInvalidException(ApplicationConstants.QR_SESSION_NOT_FOUND));

        mockMvc.perform(get("/api/auth/devices/qr-session/ghost"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSessionStatus_wrongOwner_returns403() throws Exception {
        when(sessionService.getSessionStatus(eq("session-id"), any(User.class)))
                .thenThrow(new ForbiddenOperationException(ApplicationConstants.QR_NOT_YOUR_SESSION));

        mockMvc.perform(get("/api/auth/devices/qr-session/session-id"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getSessionStatus_unauthenticated_returns401() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/api/auth/devices/qr-session/session-id"))
                .andExpect(status().isUnauthorized());
    }
}
