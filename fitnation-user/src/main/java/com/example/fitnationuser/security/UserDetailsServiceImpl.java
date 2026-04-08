package com.example.fitnationuser.security;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UsernameNotFoundException(ApplicationConstants.USER_NOT_FOUND + email));

        if (user.getStatus() == UserStatus.DELETED || user.getDeletedAt() != null) {
            throw new UsernameNotFoundException(ApplicationConstants.INVALID_CREDENTIALS);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                SecurityAuthoritiesUtil.authoritiesForRole(user.getRole())
        );
    }

}

