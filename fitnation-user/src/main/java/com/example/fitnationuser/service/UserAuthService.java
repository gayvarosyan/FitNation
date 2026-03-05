package com.example.fitnationuser.service;

import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.InvalidPasswordException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }
}
