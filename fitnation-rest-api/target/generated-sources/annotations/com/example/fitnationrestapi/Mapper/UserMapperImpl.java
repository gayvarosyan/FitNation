package com.example.fitnationrestapi.Mapper;

import com.example.fitnationcommon.dto.RegisterRequest;
import com.example.fitnationtainuser.entity.Trainer;
import com.example.fitnationuser.user.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-04T00:03:33+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setFirstName( request.firstName() );
        user.setLastName( request.lastName() );
        user.setEmail( request.email() );
        user.setPassword( request.password() );
        user.setPhone( request.phone() );
        user.setRole( request.role() );

        return user;
    }

    @Override
    public Trainer toTrainer(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        Trainer trainer = new Trainer();

        trainer.setFirstName( request.firstName() );
        trainer.setLastName( request.lastName() );
        trainer.setEmail( request.email() );
        trainer.setPassword( request.password() );
        trainer.setPhone( request.phone() );
        trainer.setRole( request.role() );
        trainer.setSpecialization( request.specialization() );
        trainer.setBio( request.bio() );

        return trainer;
    }
}
