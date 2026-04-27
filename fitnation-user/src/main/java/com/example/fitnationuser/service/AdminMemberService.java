package com.example.fitnationuser.service;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.request.CreateMemberRequest;
import com.example.fitnationcommon.dto.request.PageRequestParams;
import com.example.fitnationcommon.dto.request.UpdateMemberRequest;
import com.example.fitnationcommon.dto.response.AdminMemberStatsResponse;
import com.example.fitnationcommon.dto.response.MemberDetailResponse;
import com.example.fitnationcommon.dto.response.MemberListResponse;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.enums.UserStatus;
import com.example.fitnationcommon.exception.EmailAlreadyExistsException;
import com.example.fitnationcommon.exception.ForbiddenOperationException;
import com.example.fitnationcommon.exception.UserDeletedException;
import com.example.fitnationcommon.exception.UserNotFoundException;
import com.example.fitnationcommon.service.EmailService;
import com.example.fitnationcommon.validation.MemberValidator;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminMemberService {

    private final UserRepository userRepository;
    private final UserAdminService userAdminService;
    private final PasswordEncoder passwordEncoder;
    private final MemberValidator memberValidator;
    private final EmailService emailService;

    @Value("${fitnation.app.login-url}")
    private String loginUrl;

    public AdminMemberStatsResponse getMemberStats() {
        var totalMembers = userRepository.countTotalMembers(UserRole.CLIENT);
        var totalActiveUsers = userRepository.countByRoleAndStatus(UserRole.CLIENT, UserStatus.ACTIVE);
        var usersWithActiveSubscription = userRepository.countActiveUsersWithActiveMembership(UserRole.CLIENT, UserStatus.ACTIVE);
        var blockedMembers = userRepository.countBlockedMembers(UserRole.CLIENT, UserStatus.BLOCKED);

        return AdminMemberStatsResponse.builder()
                .totalMembers(totalMembers)
                .totalActiveUsers(totalActiveUsers)
                .usersWithActiveSubscription(usersWithActiveSubscription)
                .blockedMembers(blockedMembers)
                .premiumTierPercent(0.0)
                .build();
    }

    public PagedResponse<MemberListResponse> getMembers(Integer page, Integer size, String sort, String q, String status) {
        Pageable pageable = PageRequestParams.toPageable(page, size, sort,
                Set.of("createdAt", "firstName", "lastName", "email", "status"));

        var userStatus = status != null ? UserStatus.valueOf(status.toUpperCase()) : null;
        var hasSearch = q != null && !q.trim().isEmpty();

        Page<User> userPage;
        if (hasSearch && userStatus != null) {

            userPage = userRepository.findActiveByRoleAndStatusAndSearch(UserRole.CLIENT, userStatus, search, pageable);
        } else if (hasSearch) {
            userPage = userRepository.findActiveByRoleAndSearch(UserRole.CLIENT, search, pageable);

            userPage = userRepository.findByRoleAndStatusAndSearch(UserRole.CLIENT, userStatus, q, pageable);
        } else if (hasSearch) {
            userPage = userRepository.findByRoleAndSearch(UserRole.CLIENT, q, pageable);

        } else if (userStatus != null) {
            userPage = userRepository.findActiveByRoleAndStatus(UserRole.CLIENT, userStatus, pageable);
        } else {
            userPage = userRepository.findActiveByRole(UserRole.CLIENT, pageable);
        }

        return PagedResponse.of(userPage.map(this::convertToMemberListResponse), sort);
    }

    public MemberDetailResponse getMemberById(Long id) {
        var user = userRepository.findById(id)
                .filter(u -> u.getRole() == UserRole.CLIENT)
                .orElseThrow(() -> {
                    log.warn("getMemberById failed: member not found, id={}", id);
                    return new UserNotFoundException(ApplicationConstants.MEMBER_NOT_FOUND + id);
                });

        return convertToMemberDetailResponse(user);
    }

    public MemberDetailResponse createMember(CreateMemberRequest request) {
        memberValidator.validateCreateMemberRequest(request);

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("createMember failed: email already exists, email={}", request.getEmail());
            throw new EmailAlreadyExistsException(ApplicationConstants.EMAIL_ALREADY_EXISTS + request.getEmail());
        }

        var user = User.builder()
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

        var savedUser = userRepository.save(user);
        log.info("Created new member with id: {}", savedUser.getId());

        return convertToMemberDetailResponse(savedUser);
    }

    public MemberDetailResponse inviteMember(CreateMemberRequest request) {
        memberValidator.validateCreateMemberRequest(request);

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("inviteMember failed: email already exists, email={}", request.getEmail());
            throw new EmailAlreadyExistsException(ApplicationConstants.EMAIL_ALREADY_EXISTS + request.getEmail());
        }

        var rawPassword = request.getPassword();

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(rawPassword))
                .role(UserRole.CLIENT)
                .status(UserStatus.PENDING)
                .assignedTrainerId(request.getAssignedTrainerId())
                .assignedNutritionPlanId(request.getAssignedNutritionPlanId())
                .build();

        var savedUser = userRepository.save(user);
        log.info("Invited new member with id: {}", savedUser.getId());

        emailService.sendInvitationEmail(savedUser.getEmail(), rawPassword, loginUrl);

        return convertToMemberDetailResponse(savedUser);
    }

    public MemberDetailResponse updateMember(Long id, UpdateMemberRequest request) {
        memberValidator.validateUpdateMemberRequest(request);

        var user = userRepository.findById(id)
                .filter(u -> u.getRole() == UserRole.CLIENT)
                .orElseThrow(() -> {
                    log.warn("updateMember failed: member not found, id={}", id);
                    return new UserNotFoundException(ApplicationConstants.MEMBER_NOT_FOUND + id);
                });

        if (user.getDeletedAt() != null) {
            throw new UserDeletedException(id);
        }

        if (user.getStatus() == UserStatus.PENDING) {
            throw new ForbiddenOperationException("Cannot edit a PENDING member until they log in for the first time.");
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                log.warn("updateMember failed: email already exists, email={}", request.getEmail());
                throw new EmailAlreadyExistsException(ApplicationConstants.EMAIL_ALREADY_EXISTS + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getAssignedTrainerId() != null) {
            user.setAssignedTrainerId(request.getAssignedTrainerId());
        }
        if (request.getAssignedNutritionPlanId() != null) {
            user.setAssignedNutritionPlanId(request.getAssignedNutritionPlanId());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        var updatedUser = userRepository.save(user);
        log.info("Updated member with id: {}", updatedUser.getId());

        return convertToMemberDetailResponse(updatedUser);
    }

    public void deleteMember(Long id) {
        var user = userRepository.findById(id)
                .filter(u -> u.getRole() == UserRole.CLIENT)
                .orElseThrow(() -> {
                    log.warn("deleteMember failed: member not found, id={}", id);
                    return new UserNotFoundException(ApplicationConstants.MEMBER_NOT_FOUND + id);
                });

        userAdminService.softDeleteUser(user.getId());
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