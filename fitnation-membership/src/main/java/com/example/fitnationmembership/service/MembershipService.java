package com.example.fitnationmembership.service;

import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.request.PurchaseMembershipRequest;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.dto.response.MembershipTypeResponse;
import com.example.fitnationuser.user.User;

import java.util.List;

public interface MembershipService {

    List<MembershipTypeResponse> getAllMembershipTypes();

    MembershipTypeResponse createMembershipType(CreateMembershipTypeRequest request);

    MembershipResponse purchaseMembership(String email, PurchaseMembershipRequest request);

    List<MembershipResponse> getUserMemberships(String email);

    MembershipResponse cancelMembership(Long membershipId, User currentUser);
}
