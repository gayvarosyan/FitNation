package com.example.fitnationuser.repository;

import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationuser.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    long countByRoleAndStatus(UserRole role, UserStatus status);

    Page<User> findByRoleAndStatusContaining(UserRole role, UserStatus status, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = :role AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "u.phone LIKE CONCAT('%', :search, '%') OR " +
           "CAST(u.id AS STRING) LIKE CONCAT('%', :search, '%'))")
    Page<User> findByRoleAndSearch(@Param("role") UserRole role, @Param("search") String search, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = :status AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "u.phone LIKE CONCAT('%', :search, '%') OR " +
           "CAST(u.id AS STRING) LIKE CONCAT('%', :search, '%'))")
    Page<User> findByRoleAndStatusAndSearch(@Param("role") UserRole role, @Param("status") UserStatus status, 
                                           @Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.status = :status")
    long countActiveUsersWithActiveMembership(@Param("role") UserRole role, @Param("status") UserStatus status);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countTotalMembers(@Param("role") UserRole role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.status = :blockedStatus")
    long countBlockedMembers(@Param("role") UserRole role, @Param("blockedStatus") UserStatus blockedStatus);
}