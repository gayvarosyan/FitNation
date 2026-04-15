package com.example.fitnationbooking.service;

import com.example.fitnationbooking.mapper.ClassBookingMapper;
import com.example.fitnationbooking.repository.ClassBookingRepository;
import com.example.fitnationbooking.repository.ClassScheduleRepository;
import com.example.fitnationbooking.validation.ClassBookingValidator;
import com.example.fitnationcommon.exception.ClassScheduleNotFoundException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationuser.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassBookingServiceTest {

    @Mock
    private ClassScheduleRepository classScheduleRepository;
    @Mock
    private ClassBookingRepository classBookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ClassBookingMapper classBookingMapper;
    @Mock
    private ClassBookingValidator classBookingValidator;

    @InjectMocks
    private ClassBookingService classBookingService;

    @Test
    void bookClass_throwsWhenScheduleMissing() {
        when(classScheduleRepository.findById(5L)).thenReturn(Optional.empty());

        var ex = assertThrows(ClassScheduleNotFoundException.class, () ->
                classBookingService.bookClass(5L, 9L));
        assertTrue(ex.getMessage().contains("5"));
        verifyNoInteractions(userRepository, classBookingValidator, classBookingRepository);
    }

    @Test
    void bookClass_throwsWhenUserMissing() {
        when(classScheduleRepository.findById(5L)).thenReturn(Optional.of(new com.example.fitnationbooking.entity.ClassSchedule()));
        when(userRepository.findById(9L)).thenReturn(Optional.empty());

        var ex = assertThrows(UserNotFoundException.class, () ->
                classBookingService.bookClass(5L, 9L));
        assertTrue(ex.getMessage().contains("9"));
        verifyNoInteractions(classBookingValidator, classBookingRepository);
    }
}
