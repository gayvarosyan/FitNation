package com.example.fitnationuser.service;

import com.example.fitnationcommon.dto.request.RegisterRequest;
import com.example.fitnationcommon.exception.EmailAlreadyExistsException;
import com.example.fitnationuser.mapper.UserMapper;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        return userRepository.save(userMapper.toUser(request));
    }
}
