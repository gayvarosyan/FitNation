package com.example.fitnationbooking.validation;

import com.example.fitnationbooking.entity.ClassSchedule;
import com.example.fitnationbooking.entity.GroupClass;
import com.example.fitnationbooking.repository.ClassBookingRepository;
import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationcommon.exception.ClassBookingConflictException;
import com.example.fitnationtrainer.entity.Trainer;
import com.example.fitnationuser.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassBookingValidatorTest {

    @Mock
    private ClassBookingRepository classBookingRepository;

    @InjectMocks
    private ClassBookingValidator validator;

    private ClassSchedule schedule;
    private User user;

    @BeforeEach
    void setUp() {
        Trainer trainer = new Trainer();
        GroupClass groupClass = new GroupClass();
        groupClass.setCapacity(2);
        groupClass.setTrainer(trainer);
        schedule = new ClassSchedule();
        schedule.setGroupClass(groupClass);
        user = User.builder().id(40L).email("booker@test.com").build();
    }

    @Test
    void validateCanBook_throwsWhenClassFull() {
        when(classBookingRepository.countBySchedule_IdAndStatus(schedule.getId(), ClassBookingStatus.BOOKED)).thenReturn(2L);

        ClassBookingConflictException ex = assertThrows(ClassBookingConflictException.class, () ->
                validator.validateCanBook(schedule, user));
        assertEquals(ApplicationConstants.CLASS_SCHEDULE_FULL, ex.getMessage());
    }

    @Test
    void validateCanBook_throwsWhenDuplicateBooking() {
        when(classBookingRepository.countBySchedule_IdAndStatus(schedule.getId(), ClassBookingStatus.BOOKED)).thenReturn(0L);
        when(classBookingRepository.existsByScheduleAndUserAndStatus(schedule, user, ClassBookingStatus.BOOKED))
                .thenReturn(true);

        ClassBookingConflictException ex = assertThrows(ClassBookingConflictException.class, () ->
                validator.validateCanBook(schedule, user));
        assertEquals(ApplicationConstants.CLASS_ALREADY_BOOKED, ex.getMessage());
    }

    @Test
    void validateCanBook_succeedsWhenSeatsAvailableAndNoDuplicate() {
        when(classBookingRepository.countBySchedule_IdAndStatus(schedule.getId(), ClassBookingStatus.BOOKED)).thenReturn(1L);
        when(classBookingRepository.existsByScheduleAndUserAndStatus(schedule, user, ClassBookingStatus.BOOKED))
                .thenReturn(false);

        assertDoesNotThrow(() -> validator.validateCanBook(schedule, user));
    }
}