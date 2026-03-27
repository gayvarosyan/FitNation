package com.example.fitnationmembership.mapper;

import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.response.MembershipTypeResponse;
import com.example.fitnationmembership.model.MembershipType;
import java.math.BigDecimal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-28T01:59:12+0400",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class MembershipTypeMapperImpl implements MembershipTypeMapper {

    @Override
    public MembershipType toEntity(CreateMembershipTypeRequest request) {
        if ( request == null ) {
            return null;
        }

        MembershipType.MembershipTypeBuilder membershipType = MembershipType.builder();

        membershipType.name( request.name() );
        membershipType.durationDays( request.durationDays() );
        membershipType.price( request.price() );
        membershipType.description( request.description() );
        membershipType.nutritionPlanId( request.nutritionPlanId() );
        membershipType.trainerId( request.trainerId() );
        membershipType.groupClassId( request.groupClassId() );

        return membershipType.build();
    }

    @Override
    public void updateFromRequest(CreateMembershipTypeRequest request, MembershipType entity) {
        if ( request == null ) {
            return;
        }

        entity.setName( request.name() );
        entity.setDurationDays( request.durationDays() );
        entity.setPrice( request.price() );
        entity.setDescription( request.description() );
        entity.setNutritionPlanId( request.nutritionPlanId() );
        entity.setTrainerId( request.trainerId() );
        entity.setGroupClassId( request.groupClassId() );
    }

    @Override
    public MembershipTypeResponse toResponse(MembershipType entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        Integer durationDays = null;
        BigDecimal price = null;
        String description = null;
        Long nutritionPlanId = null;
        Long trainerId = null;
        Long groupClassId = null;

        id = entity.getId();
        name = entity.getName();
        durationDays = entity.getDurationDays();
        price = entity.getPrice();
        description = entity.getDescription();
        nutritionPlanId = entity.getNutritionPlanId();
        trainerId = entity.getTrainerId();
        groupClassId = entity.getGroupClassId();

        MembershipTypeResponse membershipTypeResponse = new MembershipTypeResponse( id, name, durationDays, price, description, nutritionPlanId, trainerId, groupClassId );

        return membershipTypeResponse;
    }
}
