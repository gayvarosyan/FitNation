package com.example.fitnationuser.security;

import com.example.fitnationcommon.enums.UserRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public final class SecurityAuthoritiesUtil {

    private SecurityAuthoritiesUtil() {
    }

    public static List<SimpleGrantedAuthority> authoritiesForRole(UserRole role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}

