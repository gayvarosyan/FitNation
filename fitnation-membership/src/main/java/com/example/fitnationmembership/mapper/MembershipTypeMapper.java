package com.example.fitnationmembership.mapper;

import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.response.MembershipTypeResponse;
import com.example.fitnationmembership.model.MembershipType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MembershipTypeMapper {

    @Mapping(target = "id", ignore = true)
    MembershipType toEntity(CreateMembershipTypeRequest request);

    MembershipTypeResponse toResponse(MembershipType entity);
}
