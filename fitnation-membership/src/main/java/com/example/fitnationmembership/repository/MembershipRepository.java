package com.example.fitnationmembership.repository;

import com.example.fitnationmembership.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Query(value = """
        SELECT m FROM Membership m
        JOIN FETCH m.membershipType
        WHERE m.user.id = :userId
        """,
            countQuery = """
        SELECT count(m) FROM Membership m
        WHERE m.user.id = :userId
        """)
    Page<Membership> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = """
        SELECT m FROM Membership m
        JOIN FETCH m.membershipType mt
        JOIN FETCH m.user u
        WHERE (:status IS NULL OR m.status = :status)
          AND (:q IS NULL OR :q = '' OR
               LOWER(u.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(u.lastName)  LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(u.email)     LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(mt.name)     LIKE LOWER(CONCAT('%', :q, '%')))
        """,
            countQuery = """
        SELECT count(m) FROM Membership m
        JOIN m.membershipType mt
        JOIN m.user u
        WHERE (:status IS NULL OR m.status = :status)
          AND (:q IS NULL OR :q = '' OR
               LOWER(u.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(u.lastName)  LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(u.email)     LIKE LOWER(CONCAT('%', :q, '%')) OR
               LOWER(mt.name)     LIKE LOWER(CONCAT('%', :q, '%')))
        """)
    Page<Membership> findAllWithFilters(
            @Param("q") String q,
            @Param("status") MembershipStatus status,
            Pageable pageable);
}
