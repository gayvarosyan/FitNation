package com.example.fitnationmembership.mapper;

import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.enums.MembershipStatus;
import com.example.fitnationmembership.model.Membership;
import com.example.fitnationmembership.model.MembershipType;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-28T01:59:12+0400",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class MembershipMapperImpl implements MembershipMapper {

    @Override
    public MembershipResponse toResponse(Membership membership) {
        if ( membership == null ) {
            return null;
        }

        String membershipType = null;
        Long membershipTypeId = null;
        Long id = null;
        LocalDate startDate = null;
        LocalDate endDate = null;
        MembershipStatus status = null;
        Long nutritionPlanId = null;
        Long trainerId = null;
        Long groupClassId = null;

        membershipType = membershipMembershipTypeName( membership );
        membershipTypeId = membershipMembershipTypeId( membership );
        id = membership.getId();
        startDate = membership.getStartDate();
        endDate = membership.getEndDate();
        status = membership.getStatus();
        nutritionPlanId = membership.getNutritionPlanId();
        trainerId = membership.getTrainerId();
        groupClassId = membership.getGroupClassId();

        MembershipResponse membershipResponse = new MembershipResponse( id, membershipType, membershipTypeId, startDate, endDate, status, nutritionPlanId, trainerId, groupClassId );

        return membershipResponse;
    }

    private String membershipMembershipTypeName(Membership membership) {
        MembershipType membershipType = membership.getMembershipType();
        if ( membershipType == null ) {
            return null;
        }
        return membershipType.getName();
    }

    private Long membershipMembershipTypeId(Membership membership) {
        MembershipType membershipType = membership.getMembershipType();
        if ( membershipType == null ) {
            return null;
        }
        return membershipType.getId();
    }
}
