package com.example.fitnationbooking.service;

import com.example.fitnationbooking.entity.ClassSchedule;
import com.example.fitnationbooking.mapper.GroupClassMapper;
import com.example.fitnationbooking.repository.ClassBookingRepository;
import com.example.fitnationbooking.repository.ClassScheduleRepository;
import com.example.fitnationbooking.repository.GroupClassRepository;
import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.request.ClassScheduleFilterRequest;
import com.example.fitnationcommon.dto.request.CreateGroupClassRequest;
import com.example.fitnationcommon.dto.request.PageRequestParams;
import com.example.fitnationcommon.dto.request.ScheduleClassRequest;
import com.example.fitnationcommon.dto.request.UpdateGroupClassRequest;
import com.example.fitnationcommon.dto.response.ClassScheduleItemResponse;
import com.example.fitnationcommon.dto.response.GroupClassResponse;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationcommon.exception.ClassScheduleNotFoundException;
import com.example.fitnationcommon.exception.GroupClassNotFoundException;
import com.example.fitnationcommon.exception.InvalidFilterException;
import com.example.fitnationcommon.exception.TrainerNotFoundException;
import com.example.fitnationtrainer.repository.TrainerRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupClassService {

    private final GroupClassRepository groupClassRepository;
    private final ClassBookingRepository classBookingRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final TrainerRepository trainerRepository;
    private final GroupClassMapper groupClassMapper;
    private final EntityManager entityManager;

    @Transactional
    public GroupClassResponse createClass(CreateGroupClassRequest request) {
        var trainer = trainerRepository.findById(request.trainerId())
                .orElseThrow(() -> new TrainerNotFoundException(
                        ApplicationConstants.MSG_TRAINER_NOT_FOUND + request.trainerId()));

        var groupClass = groupClassMapper.toEntity(request, trainer);
        var saved = groupClassRepository.save(groupClass);
        return groupClassMapper.toGroupClassResponse(saved);
    }

    @Transactional
    public ClassScheduleItemResponse scheduleClass(Long classId, ScheduleClassRequest request) {
        var groupClass = groupClassRepository.findById(classId)
                .orElseThrow(() -> new GroupClassNotFoundException(
                        ApplicationConstants.MSG_CLASS_NOT_FOUND + classId));

        var schedule = groupClassMapper.toSchedule(groupClass, request);
        var saved = classScheduleRepository.save(schedule);
        var loaded = classScheduleRepository.findByIdWithClassAndTrainer(saved.getId())
                .orElseThrow(() -> new ClassScheduleNotFoundException(
                        ApplicationConstants.MSG_SCHEDULE_NOT_FOUND_AFTER_SAVE + saved.getId()));
        return groupClassMapper.toScheduleItemResponse(loaded, 0L);
    }

    @Transactional
    public GroupClassResponse updateClass(Long classId, UpdateGroupClassRequest request) {
        var groupClass = groupClassRepository.findById(classId)
                .orElseThrow(() -> new GroupClassNotFoundException(
                        ApplicationConstants.MSG_CLASS_NOT_FOUND + classId));

        var trainer = trainerRepository.findById(request.trainerId())
                .orElseThrow(() -> new TrainerNotFoundException(
                        ApplicationConstants.MSG_TRAINER_NOT_FOUND + request.trainerId()));

        groupClassMapper.updateEntity(groupClass, request, trainer);

        var saved = groupClassRepository.save(groupClass);
        return groupClassMapper.toGroupClassResponse(saved);
    }

    @Transactional
    public ClassScheduleItemResponse updateSchedule(Long scheduleId, ScheduleClassRequest request) {
        var schedule = classScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ClassScheduleNotFoundException(
                        ApplicationConstants.MSG_SCHEDULE_NOT_FOUND + scheduleId));

        groupClassMapper.updateSchedule(schedule, request);

        var saved = classScheduleRepository.save(schedule);
        var loaded = classScheduleRepository.findByIdWithClassAndTrainer(saved.getId())
                .orElseThrow(() -> new ClassScheduleNotFoundException(
                        ApplicationConstants.MSG_SCHEDULE_NOT_FOUND_AFTER_UPDATE + saved.getId()));
        return groupClassMapper.toScheduleItemResponse(loaded, 0L);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        classScheduleRepository.deleteById(scheduleId);
    }

    @Transactional
    public List<ClassScheduleItemResponse> getAllSchedules(ClassScheduleFilterRequest filter) {
        if (filter != null && filter.fromDate() != null && filter.toDate() != null) {
            if (filter.fromDate().isAfter(filter.toDate())) {
                throw new InvalidFilterException(ApplicationConstants.INVALID_FILTER_DATE_RANGE);
            }
        }

        var fromDate = filter != null ? filter.fromDate() : null;
        var toDate = filter != null ? filter.toDate() : null;
        var trainerId = filter != null ? filter.trainerId() : null;
        var schedules = classScheduleRepository.findAllWithFilters(
                fromDate, toDate, trainerId, null);

        return schedules.stream()
                .filter(s -> filterByStatus(s, filter))
                .map(s -> {
                    long booked = classBookingRepository.countByScheduleAndStatus(s.getId(), ClassBookingStatus.BOOKED.name());
                    return groupClassMapper.toScheduleItemResponse(s, booked);
                })
                .toList();
    }

    @Transactional
    public PagedResponse<ClassScheduleItemResponse> getAllSchedules(
            Integer page, Integer size, String sort,
            String q, LocalDate dateFrom, LocalDate dateTo, Long trainerId) {

        var pageable = PageRequestParams.toPageable(page, size, sort,
                Set.of("date", "startTime", "createdAt"));

        var resultPage = classScheduleRepository
                .findAllWithFilters(q, dateFrom, dateTo, trainerId, pageable)
                .map(s -> {
                    long booked = classBookingRepository.countByScheduleAndStatus(s.getId(), ClassBookingStatus.BOOKED.name());
                    return groupClassMapper.toScheduleItemResponse(s, booked);
                });

        return PagedResponse.of(resultPage, sort);
    }

    @Transactional
    public List<GroupClassResponse> listAllGroupClasses() {
        return groupClassRepository.findAllWithTrainer().stream()
                .map(groupClassMapper::toGroupClassResponse)
                .toList();
    }

    @Transactional
    public void deleteAllByTrainerId(Long trainerId) {
        classBookingRepository.deleteAllByTrainerId(trainerId);
        entityManager.flush();

        classScheduleRepository.deleteAllByTrainerId(trainerId);
        entityManager.flush();

        groupClassRepository.deleteAllByTrainerId(trainerId);
        entityManager.flush();
    }

    private boolean filterByStatus(ClassSchedule s, ClassScheduleFilterRequest filter) {
        if (filter == null || filter.status() == null) return true;

        var now = LocalDateTime.now();
        var start = LocalDateTime.of(s.getDate(), s.getStartTime());
        var end = LocalDateTime.of(s.getDate(), s.getEndTime());
        var booked = classBookingRepository.countByScheduleAndStatus(s.getId(), ClassBookingStatus.BOOKED.name());
        int capacity = s.getGroupClass().getCapacity() != null ? s.getGroupClass().getCapacity() : 0;

        return switch (filter.status()) {
            case UPCOMING -> now.isBefore(start);
            case IN_PROGRESS -> !now.isBefore(start) && now.isBefore(end);
            case COMPLETED -> !now.isBefore(end);
            case FULL -> booked >= capacity && capacity > 0;
        };
    }

    private Sort parseSort(String sort) {
        if (sort == null || !sort.contains(",")) {
            return Sort.by(Sort.Direction.ASC, "date");
        }
        String[] parts = sort.split(",", 2);
        Sort.Direction direction = parts[1].trim().equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, parts[0].trim());
    }
}