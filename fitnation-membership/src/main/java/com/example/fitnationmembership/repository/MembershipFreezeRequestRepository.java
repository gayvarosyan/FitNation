package com.example.fitnationmembership.repository;

import com.example.fitnationcommon.enums.FreezeRequestStatus;
import com.example.fitnationmembership.model.MembershipFreezeRequests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembershipFreezeRequestRepository extends JpaRepository<MembershipFreezeRequests, Long> {

    boolean existsByMembershipIdAndStatusInAndFreezeEndAfterAndFreezeStartBefore(
            Long membershipId,
            List<FreezeRequestStatus> statuses,
            LocalDate freezeStart,
            LocalDate freezeEnd
    );

    @Query("SELECT r FROM MembershipFreezeRequests r JOIN FETCH r.membership m JOIN FETCH m.membershipType WHERE m.id = :membershipId ORDER BY r.createdAt DESC")
    List<MembershipFreezeRequests> findAllByMembershipId(@Param("membershipId") Long membershipId);

    @Query("SELECT r FROM MembershipFreezeRequests r JOIN FETCH r.membership m JOIN FETCH m.membershipType JOIN FETCH m.user LEFT JOIN FETCH r.reviewedBy WHERE r.id = :id")
    Optional<MembershipFreezeRequests> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT r FROM MembershipFreezeRequests r JOIN FETCH r.membership m JOIN FETCH m.membershipType JOIN FETCH m.user LEFT JOIN FETCH r.reviewedBy ORDER BY r.createdAt DESC")
    Page<MembershipFreezeRequests> findAllWithDetails(Pageable pageable);

    @Query("SELECT r FROM MembershipFreezeRequests r JOIN FETCH r.membership m JOIN FETCH m.membershipType JOIN FETCH m.user LEFT JOIN FETCH r.reviewedBy WHERE r.status = :status ORDER BY r.createdAt DESC")
    Page<MembershipFreezeRequests> findAllWithDetailsByStatus(@Param("status") FreezeRequestStatus status, Pageable pageable);
}
