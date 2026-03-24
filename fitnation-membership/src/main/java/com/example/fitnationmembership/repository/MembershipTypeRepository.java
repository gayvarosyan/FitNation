package com.example.fitnationmembership.repository;

import com.example.fitnationmembership.model.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipTypeRepository extends JpaRepository<MembershipType, Long> {
}
