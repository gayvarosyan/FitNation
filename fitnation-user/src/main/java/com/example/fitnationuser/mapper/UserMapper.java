package com.example.fitnationuser.mapper;

import com.example.fitnationcommon.dto.request.RegisterRequest;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toUser(RegisterRequest request) {
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.CLIENT);
        user.setStatus(UserStatus.INACTIVE);
        return user;
    }
}
