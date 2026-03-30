package com.example.fitnationmembership.model;

import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationuser.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "memberships")
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_type_id", nullable = false)
    private MembershipType membershipType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private MembershipStatus status;

    @Column(name = "nutrition_plan_id")
    private Long nutritionPlanId;

    @Column(name = "trainer_id")
    private Long trainerId;

    @Column(name = "group_class_id")
    private Long groupClassId;

    public void markExpired() {
        this.status = MembershipStatus.EXPIRED;
    }

    public void update(MembershipType membershipType,
                       LocalDate startDate,
                       LocalDate endDate,
                       MembershipStatus status,
                       Long nutritionPlanId,
                       Long trainerId,
                       Long groupClassId) {
        this.membershipType = membershipType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.nutritionPlanId = nutritionPlanId;
        this.trainerId = trainerId;
        this.groupClassId = groupClassId;
    }
}
