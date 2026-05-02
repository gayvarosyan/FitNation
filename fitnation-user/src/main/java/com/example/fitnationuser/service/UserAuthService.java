package com.example.fitnationuser.service;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.InvalidPasswordException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationuser.validation.SoftDeleteValidationService;
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
    private final UserStatusUtil userStatusUtil;
    private final SoftDeleteValidationService softDeleteValidationService;

    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UserNotFoundException(ApplicationConstants.MSG_USER_NOT_FOUND + email));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidPasswordException(ApplicationConstants.PASSWORD_INVALID);
        }

        softDeleteValidationService.validateUserForAuthentication(user);
        userStatusUtil.ensureActive(user);

        if (user.getStatus() == UserStatus.PENDING) {
            user.setStatus(UserStatus.ACTIVE);
            return userRepository.save(user);
        }
        
        return user;
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UserNotFoundException(ApplicationConstants.MSG_USER_NOT_FOUND));
        
        softDeleteValidationService.validateUserNotSoftDeleted(user);
        
        return user;
    }
}
