package com.example.fitnationbooking.repository;

import com.example.fitnationbooking.entity.ClassBooking;
import com.example.fitnationbooking.entity.ClassSchedule;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationuser.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {

    long countByScheduleAndStatus(ClassSchedule schedule, ClassBookingStatus status);

    boolean existsByScheduleAndUserAndStatus(ClassSchedule schedule, User user, ClassBookingStatus status);

    List<ClassBooking> findByUser(User user);

    Optional<ClassBooking> findByIdAndUser(Long id, User user);

    List<ClassBooking> findAllByScheduleGroupClassTrainerId(Long trainerId);

    @Modifying
    @Query("""
            delete from ClassBooking cb
            where cb.schedule.id in (
                select s.id from ClassSchedule s
                where s.groupClass.trainer.id = :trainerId
            )
            """)
    void deleteAllByTrainerId(Long trainerId);
}