package com.example.fitnationbooking.service;

import com.example.fitnationbooking.mapper.GroupClassMapper;
import com.example.fitnationbooking.repository.ClassBookingRepository;
import com.example.fitnationbooking.repository.ClassScheduleRepository;
import com.example.fitnationbooking.repository.GroupClassRepository;
import com.example.fitnationcommon.dto.request.CreateGroupClassRequest;
import com.example.fitnationcommon.exception.TrainerNotFoundException;
import com.example.fitnationtrainer.repository.TrainerRepository;
import jakarta.persistence.EntityManager;
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
class GroupClassServiceTest {

    @Mock
    private GroupClassRepository groupClassRepository;
    @Mock
    private ClassBookingRepository classBookingRepository;
    @Mock
    private ClassScheduleRepository classScheduleRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private GroupClassMapper groupClassMapper;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private GroupClassService groupClassService;

    @Test
    void createClass_throwsWhenTrainerMissing() {
        when(trainerRepository.findById(404L)).thenReturn(Optional.empty());

        var ex = assertThrows(TrainerNotFoundException.class, () ->
                groupClassService.createClass(new CreateGroupClassRequest("HIIT", "desc", 12, 404L)));
        assertTrue(ex.getMessage().contains("404"));
        verifyNoInteractions(groupClassRepository, groupClassMapper);
    }
}
