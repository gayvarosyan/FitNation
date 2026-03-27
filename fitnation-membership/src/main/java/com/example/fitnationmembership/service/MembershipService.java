package com.example.fitnationmembership.service;

import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.request.PurchaseMembershipRequest;
import com.example.fitnationcommon.dto.request.UpdateMembershipRequest;
import com.example.fitnationcommon.dto.response.AdminMembershipRecordResponse;
import com.example.fitnationcommon.dto.response.AdminMembershipStatsResponse;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.dto.response.MembershipTypeResponse;
import com.example.fitnationuser.user.User;

import java.util.List;

public interface MembershipService {

    List<MembershipTypeResponse> getAllMembershipTypes();

    MembershipTypeResponse createMembershipType(CreateMembershipTypeRequest request);

    MembershipTypeResponse updateMembershipType(Long id, CreateMembershipTypeRequest request);

    void deleteMembershipType(Long id);

    MembershipResponse purchaseMembership(String email, PurchaseMembershipRequest request);

    List<MembershipResponse> getUserMemberships(String email);

    MembershipResponse cancelMembership(Long membershipId, User currentUser);

    MembershipResponse updateMembership(Long membershipId, UpdateMembershipRequest request, User currentUser);

    List<AdminMembershipRecordResponse> getAdminMemberships();

    AdminMembershipStatsResponse getAdminMembershipStats();
}
