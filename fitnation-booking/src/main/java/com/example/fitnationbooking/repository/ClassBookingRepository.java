package com.example.fitnationbooking.repository;

import com.example.fitnationbooking.entity.ClassBooking;
import com.example.fitnationbooking.entity.ClassSchedule;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationuser.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {

    long countByScheduleAndStatus(ClassSchedule schedule, ClassBookingStatus status);

    boolean existsByScheduleAndUserAndStatus(ClassSchedule schedule, User user, ClassBookingStatus status);

    List<ClassBooking> findByUser(User user);

    Optional<ClassBooking> findByIdAndUser(Long id, User user);
}