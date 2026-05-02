package com.example.fitnationuser.security;

import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.service.UserStatusUtil;
import com.example.fitnationuser.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserStatusUtil userStatusUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Optional<String> token = resolveToken(request);
        if (token.isEmpty()) {
            proceed(filterChain, request, response);
            return;
        }

        log.debug("JWT request {} {}", request.getMethod(), request.getRequestURI());

        if (!jwtService.isAccessTokenValid(token.get())) {
            log.warn("Invalid or expired JWT token - request: {} {}", request.getMethod(), request.getRequestURI());
            proceed(filterChain, request, response);
            return;
        }

        establishAuthenticationIfNeeded(token.get(), request);
        proceed(filterChain, request, response);
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        return bearerAccessToken(request.getHeader("Authorization"))
                .or(() -> sessionAccessToken(request));
    }

    private static Optional<String> bearerAccessToken(String authorizationHeader) {
        return Optional.ofNullable(authorizationHeader)
                .filter(h -> h.startsWith("Bearer "))
                .map(h -> h.substring(7));
    }

    private static Optional<String> sessionAccessToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }
        Object raw = session.getAttribute(JwtSessionConstants.ACCESS_TOKEN);
        return raw instanceof String s && !s.isBlank()
                ? Optional.of(s)
                : Optional.empty();
    }

    private void establishAuthenticationIfNeeded(String token, HttpServletRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }
        String email = jwtService.extractEmail(token);
        if (email == null) {
            return;
        }

        userRepository.findByEmail(email).ifPresentOrElse(
                user -> authenticateUser(user, request),
                () -> log.warn("JWT token valid but user not found - email: {}", email));
    }

    private void authenticateUser(User user, HttpServletRequest request) {
        if (userStatusUtil.isBlockedOrInactive(user)) {
            log.warn("Blocked or inactive user attempted access - email: {}", user.getEmail());
            return;
        }
        setAuthentication(user, request);
    }

    private static void setAuthentication(User user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                SecurityAuthoritiesUtil.authoritiesForRole(user.getRole()));
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private static void proceed(FilterChain filterChain,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }
}
