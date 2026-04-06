package com.example.fitnationmembership.repository;

import com.example.fitnationmembership.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fitnationcommon.enums.MembershipStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    long countByMembershipType_Id(Long membershipTypeId);

    boolean existsByUser_IdAndMembershipType_IdAndStatusAndEndDateGreaterThanEqual(
            Long userId,
            Long membershipTypeId,
            MembershipStatus status,
            LocalDate endDateMin);

    @Query("SELECT m FROM Membership m JOIN FETCH m.membershipType WHERE m.user.id = :userId")
    List<Membership> findAllByUserIdWithType(@Param("userId") Long userId);

    @Query("SELECT m FROM Membership m JOIN FETCH m.membershipType JOIN FETCH m.user WHERE m.id = :id")
    Optional<Membership> findByIdWithTypeAndUser(@Param("id") Long id);

    @Query("SELECT m FROM Membership m JOIN FETCH m.membershipType JOIN FETCH m.user")
    List<Membership> findAllWithTypeAndUser();
}
