package com.example.fitnationrestapi.support;

import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationuser.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserHelper {

    public User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    public Long getId() {
        return getUser().getId();
    }

    public UserRole getRole() {
        return getUser().getRole();
    }
}