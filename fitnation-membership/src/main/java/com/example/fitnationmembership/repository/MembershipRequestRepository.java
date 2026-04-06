package com.example.fitnationmembership.repository;

import com.example.fitnationcommon.enums.MembershipRequestStatus;
import com.example.fitnationmembership.model.MembershipRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MembershipRequestRepository extends JpaRepository<MembershipRequest, Long> {

    boolean existsByUser_IdAndMembershipType_IdAndStatus(
            Long userId,
            Long membershipTypeId,
            MembershipRequestStatus status);

    @Query("""
            SELECT r FROM MembershipRequest r
            JOIN FETCH r.membershipType
            WHERE r.user.id = :userId
            ORDER BY r.createdAt DESC
            """)
    List<MembershipRequest> findAllByUserIdWithType(@Param("userId") Long userId);

    @Query("""
            SELECT r FROM MembershipRequest r
            JOIN FETCH r.user
            JOIN FETCH r.membershipType
            LEFT JOIN FETCH r.reviewedBy
            WHERE r.id = :id
            """)
    Optional<MembershipRequest> findByIdWithDetails(@Param("id") Long id);

    @EntityGraph("MembershipRequest.withDetails")
    Page<MembershipRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph("MembershipRequest.withDetails")
    Page<MembershipRequest> findAllByStatusOrderByCreatedAtDesc(
            MembershipRequestStatus status,
            Pageable pageable);
}
