package com.example.fitnationmembership.mapper;

import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.response.MembershipTypeResponse;
import com.example.fitnationmembership.model.MembershipType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MembershipTypeMapper {

    @Mapping(target = "id", ignore = true)
    MembershipType toEntity(CreateMembershipTypeRequest request);

    @Mapping(target = "id", ignore = true)
    void updateFromRequest(CreateMembershipTypeRequest request, @MappingTarget MembershipType entity);

    MembershipTypeResponse toResponse(MembershipType entity);
}
