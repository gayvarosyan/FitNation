package com.example.fitnationbooking.repository;

import com.example.fitnationbooking.entity.ClassBooking;
import com.example.fitnationbooking.entity.ClassSchedule;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationuser.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {

    long countBySchedule_IdAndStatus(Long scheduleId, ClassBookingStatus status);

    boolean existsByScheduleAndUserAndStatus(ClassSchedule schedule, User user, ClassBookingStatus status);

    Page<ClassBooking> findByUser(User user, Pageable pageable);
    Page<ClassBooking> findByUserAndStatus(User user, ClassBookingStatus status, Pageable pageable);

    @Query("SELECT cb FROM ClassBooking cb LEFT JOIN FETCH cb.user LEFT JOIN FETCH cb.schedule s LEFT JOIN FETCH s.groupClass gc LEFT JOIN FETCH gc.trainer WHERE cb.id = :id AND cb.user.id = :userId")
    Optional<ClassBooking> findByIdAndUserWithDetails(@Param("id") Long id, @Param("userId") Long userId);

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