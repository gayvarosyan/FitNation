package com.example.fitnationmembership.service;

import com.example.fitnationcommon.dto.request.CreateMembershipTypeRequest;
import com.example.fitnationcommon.dto.request.PurchaseMembershipRequest;
import com.example.fitnationcommon.dto.request.RejectMembershipRequest;
import com.example.fitnationcommon.dto.request.SubmitMembershipRequest;
import com.example.fitnationcommon.dto.request.UpdateMembershipRequest;
import com.example.fitnationcommon.dto.response.AdminMembershipRecordResponse;
import com.example.fitnationcommon.dto.response.AdminMembershipRequestResponse;
import com.example.fitnationcommon.dto.response.AdminMembershipStatsResponse;
import com.example.fitnationcommon.dto.response.MembershipResponse;
import com.example.fitnationcommon.dto.response.MembershipTypeResponse;
import com.example.fitnationcommon.dto.response.UserMembershipRequestResponse;
import com.example.fitnationcommon.enums.MembershipRequestStatus;
import com.example.fitnationuser.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface MembershipService {

    List<MembershipTypeResponse> getAllMembershipTypes();

    MembershipTypeResponse createMembershipType(CreateMembershipTypeRequest request);

    MembershipTypeResponse updateMembershipType(Long id, CreateMembershipTypeRequest request);

    void deleteMembershipType(Long id);

    MembershipResponse purchaseMembership(String email, PurchaseMembershipRequest request);

    Page<MembershipResponse> getUserMemberships(String email, Pageable pageable);

    UserMembershipRequestResponse submitMembershipRequest(String userEmail, SubmitMembershipRequest request);

    List<UserMembershipRequestResponse> getUserMembershipRequests(String userEmail);

    Page<AdminMembershipRequestResponse> listMembershipRequestsForAdmin(
            MembershipRequestStatus statusFilter,
            Pageable pageable);

    AdminMembershipRequestResponse approveMembershipRequest(Long requestId, User reviewer);

    AdminMembershipRequestResponse rejectMembershipRequest(
            Long requestId,
            User reviewer,
            RejectMembershipRequest rejectBody);

    MembershipResponse cancelMembership(Long membershipId, User currentUser);

    MembershipResponse updateMembership(Long membershipId, UpdateMembershipRequest request, User currentUser);

    Page<AdminMembershipRecordResponse> getAdminMemberships(Pageable pageable, String q, String status);

    AdminMembershipStatsResponse getAdminMembershipStats();
}
