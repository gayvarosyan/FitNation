package com.example.fitnationuser.service;

import com.example.fitnationcommon.dto.request.CreateMemberRequest;
import com.example.fitnationcommon.dto.request.MemberSearchRequest;
import com.example.fitnationcommon.dto.request.UpdateMemberRequest;
import com.example.fitnationcommon.dto.response.AdminMemberStatsResponse;
import com.example.fitnationcommon.dto.response.MemberDetailResponse;
import com.example.fitnationcommon.dto.response.MemberListResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.validation.MemberValidator;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminMemberService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberValidator memberValidator;

    public AdminMemberStatsResponse getMemberStats() {
        try {
            AdminMemberStatsResponse stats = new AdminMemberStatsResponse();
            
            long totalActiveUsers = userRepository.countByRoleAndStatus(UserRole.CLIENT, UserStatus.ACTIVE);
            stats.setTotalActiveUsers(totalActiveUsers);
            
            long usersWithActiveSubscription = userRepository.countActiveUsersWithActiveMembership(UserRole.CLIENT, UserStatus.ACTIVE);
            stats.setUsersWithActiveSubscription(usersWithActiveSubscription);
            
            long totalMembers = userRepository.countTotalMembers(UserRole.CLIENT);
            stats.setTotalMembers(totalMembers);
            
            long blockedMembers = userRepository.countBlockedMembers(UserRole.CLIENT, UserStatus.BLOCKED);
            stats.setBlockedMembers(blockedMembers);
            
            if (totalActiveUsers > 0) {
                stats.setPremiumTierPercent(0.0);
            } else {
                stats.setPremiumTierPercent(0.0);
            }
            
            return stats;
        } catch (Exception e) {
            log.error("Error calculating member stats", e);
            return AdminMemberStatsResponse.empty();
        }
    }

    public Page<MemberListResponse> getMembers(MemberSearchRequest searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        Page<User> userPage;
        
        if (searchRequest.getSearch() != null && !searchRequest.getSearch().trim().isEmpty()) {
            if (searchRequest.getStatus() != null) {
                userPage = userRepository.findByRoleAndStatusAndSearch(
                    UserRole.CLIENT, searchRequest.getStatus(), searchRequest.getSearch(), pageable);
            } else {
                userPage = userRepository.findByRoleAndSearch(UserRole.CLIENT, searchRequest.getSearch(), pageable);
            }
        } else if (searchRequest.getStatus() != null) {
            userPage = userRepository.findByRoleAndStatusContaining(UserRole.CLIENT, searchRequest.getStatus(), pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        
        return userPage.map(this::convertToMemberListResponse);
    }

    public MemberDetailResponse getMemberById(Long id) {
        User user = userRepository.findById(id)
            .filter(u -> u.getRole() == UserRole.CLIENT)
            .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        
        return convertToMemberDetailResponse(user);
    }

    public MemberDetailResponse createMember(CreateMemberRequest request) {
        memberValidator.validateCreateMemberRequest(request);
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CLIENT);
        user.setStatus(UserStatus.ACTIVE);
        user.setAssignedTrainerId(request.getAssignedTrainerId());
        user.setAssignedNutritionPlanId(request.getAssignedNutritionPlanId());
        
        User savedUser = userRepository.save(user);
        log.info("Created new member with id: {}", savedUser.getId());
        
        return convertToMemberDetailResponse(savedUser);
    }

    public MemberDetailResponse updateMember(Long id, UpdateMemberRequest request) {
        memberValidator.validateUpdateMemberRequest(request);
        
        User user = userRepository.findById(id)
            .filter(u -> u.getRole() == UserRole.CLIENT)
            .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        user.setAssignedTrainerId(request.getAssignedTrainerId());
        user.setAssignedNutritionPlanId(request.getAssignedNutritionPlanId());
        
        User updatedUser = userRepository.save(user);
        log.info("Updated member with id: {}", updatedUser.getId());
        
        return convertToMemberDetailResponse(updatedUser);
    }

    public void deleteMember(Long id) {
        User user = userRepository.findById(id)
            .filter(u -> u.getRole() == UserRole.CLIENT)
            .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        
        user.setStatus(UserStatus.BLOCKED);
        user.setEmail("deleted-user-" + id + "@deleted.com");
        user.setFirstName("Deleted");
        user.setLastName("User");
        user.setPhone("0000000000");
        
        userRepository.save(user);
        log.info("Soft deleted member with id: {}", id);
    }

    private MemberListResponse convertToMemberListResponse(User user) {
        MemberListResponse response = new MemberListResponse();
        response.setId(user.getId());
        response.setFormattedId("USR-" + user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setJoinDate(user.getCreatedAt());
        response.setUserStatus(user.getStatus().toString());
        
        return response;
    }

    private MemberDetailResponse convertToMemberDetailResponse(User user) {
        MemberDetailResponse response = new MemberDetailResponse();
        response.setId(user.getId());
        response.setFormattedId("USR-" + user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setUserStatus(user.getStatus().toString());
        response.setJoinDate(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        return response;
    }
}
