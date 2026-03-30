package com.example.fitnationmembership.mapper;

import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationmembership.model.Membership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MembershipMapper {

    @Mapping(target = "membershipType", source = "membershipType.name")
    @Mapping(target = "membershipTypeId", source = "membershipType.id")
    MembershipResponse toResponse(Membership membership);
}
