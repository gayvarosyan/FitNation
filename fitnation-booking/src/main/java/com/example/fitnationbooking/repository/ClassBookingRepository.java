package com.example.fitnationbooking.repository;

import com.example.fitnationbooking.entity.ClassBooking;
import com.example.fitnationbooking.entity.ClassSchedule;
import com.example.fitnationcommon.enums.ClassBookingStatus;
import com.example.fitnationuser.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {

    @Query(value = "select count(*) from public.class_bookings where class_schedule_id = :scheduleId and status = cast(:status as booking_status)", nativeQuery = true)
    long countByScheduleAndStatus(@Param("scheduleId") Long scheduleId, @Param("status") String status);

    boolean existsByScheduleAndUserAndStatus(ClassSchedule schedule, User user, ClassBookingStatus status);

    Page<ClassBooking> findByUser(User user, Pageable pageable);
    Page<ClassBooking> findByUserAndStatus(User user, ClassBookingStatus status, Pageable pageable);

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