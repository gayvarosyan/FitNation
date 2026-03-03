package com.example.fitnationrestapi.Mapper;

import com.example.fitnationcommon.dto.RegisterRequest;
import com.example.fitnationtainuser.entity.Trainer;
import com.example.fitnationuser.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(RegisterRequest request);

    Trainer toTrainer(RegisterRequest request);
}