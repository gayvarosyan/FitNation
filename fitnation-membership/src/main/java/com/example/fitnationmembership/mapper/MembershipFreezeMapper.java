package com.example.fitnationmembership.mapper;

import com.example.fitnationcommon.dto.response.AdminFreezeRequestResponse;
import com.example.fitnationcommon.dto.response.UserFreezeRequestResponse;
import com.example.fitnationmembership.model.MembershipFreezeRequests;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MembershipFreezeMapper {

    @Mapping(target = "membershipId",       source = "membership.id")
    @Mapping(target = "membershipTypeName", source = "membership.membershipType.name")
    @Mapping(target = "membershipStartDate",source = "membership.startDate")
    @Mapping(target = "membershipEndDate",  source = "membership.endDate")
    @Mapping(target = "membershipStatus",   source = "membership.status")
    UserFreezeRequestResponse toUserResponse(MembershipFreezeRequests freezeRequest);

    @Mapping(target = "membershipId",       source = "membership.id")
    @Mapping(target = "membershipTypeName", source = "membership.membershipType.name")
    @Mapping(target = "userId",             source = "membership.user.id")
    @Mapping(target = "userFirstName",      source = "membership.user.firstName")
    @Mapping(target = "userLastName",       source = "membership.user.lastName")
    @Mapping(target = "userEmail",          source = "membership.user.email")
    @Mapping(target = "reviewedById",       source = "reviewedBy.id")
    AdminFreezeRequestResponse toAdminResponse(MembershipFreezeRequests freezeRequest);
}