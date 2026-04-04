package com.example.fitnationuser.service;

import com.example.fitnationcommon.dto.request.CreateMemberRequest;
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
            long totalMembers = userRepository.countTotalMembers(UserRole.CLIENT);
            long totalActiveUsers = userRepository.countByRoleAndStatus(UserRole.CLIENT, UserStatus.ACTIVE);
            long usersWithActiveSubscription = userRepository.countActiveUsersWithActiveMembership(UserRole.CLIENT, UserStatus.ACTIVE);
            long blockedMembers = userRepository.countBlockedMembers(UserRole.CLIENT, UserStatus.BLOCKED);

            return AdminMemberStatsResponse.builder()
                    .totalMembers(totalMembers)
                    .totalActiveUsers(totalActiveUsers)
                    .usersWithActiveSubscription(usersWithActiveSubscription)
                    .blockedMembers(blockedMembers)
                    .premiumTierPercent(0.0)
                    .build();
        }

        public Page<MemberListResponse> getMembers(Integer page, Integer size, String search, String status) {
            Pageable pageable = PageRequest.of(page, size);
            UserStatus userStatus = status != null ? UserStatus.valueOf(status.toUpperCase()) : null;
            boolean hasSearch = search != null && !search.trim().isEmpty();

            Page<User> userPage;
            if (hasSearch && userStatus != null) {
                userPage = userRepository.findByRoleAndStatusAndSearch(UserRole.CLIENT, userStatus, search, pageable);
            } else if (hasSearch) {
                userPage = userRepository.findByRoleAndSearch(UserRole.CLIENT, search, pageable);
            } else if (userStatus != null) {
                userPage = userRepository.findByRoleAndStatus(UserRole.CLIENT, userStatus, pageable);
            } else {
                userPage = userRepository.findByRole(UserRole.CLIENT, pageable);
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

            User user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(UserRole.CLIENT)
                    .status(UserStatus.ACTIVE)
                    .assignedTrainerId(request.getAssignedTrainerId())
                    .assignedNutritionPlanId(request.getAssignedNutritionPlanId())
                    .build();

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

            if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
            if (request.getLastName() != null) user.setLastName(request.getLastName());
            if (request.getPhone() != null) user.setPhone(request.getPhone());
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
            return MemberListResponse.builder()
                    .id(user.getId())
                    .formattedId("USR-" + user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .joinDate(user.getCreatedAt())
                    .userStatus(user.getStatus().toString())
                    .build();
        }

        private MemberDetailResponse convertToMemberDetailResponse(User user) {
            return MemberDetailResponse.builder()
                    .id(user.getId())
                    .formattedId("USR-" + user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .userStatus(user.getStatus().toString())
                    .joinDate(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        }
    }