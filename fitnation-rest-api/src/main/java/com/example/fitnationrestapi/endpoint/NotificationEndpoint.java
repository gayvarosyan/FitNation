package com.example.fitnationrestapi.endpoint;

import com.example.fitnationprogress.dto.InAppNotificationResponse;
import com.example.fitnationprogress.service.InAppNotificationQueryService;
import com.example.fitnationrestapi.support.CurrentUserHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CLIENT', 'TRAINER', 'ADMIN')")
@Tag(name = "Notifications", description = "In-app notifications for the authenticated user")
public class NotificationEndpoint {

    private final InAppNotificationQueryService inAppNotificationQueryService;
    private final CurrentUserHelper currentUserHelper;

    @Operation(summary = "List my in-app notifications (newest first)")
    @GetMapping
    public ResponseEntity<Page<InAppNotificationResponse>> list(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<InAppNotificationResponse> page =
                inAppNotificationQueryService.listForRecipient(currentUserHelper.getId(), pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Mark a notification as read")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "Notification not found for this user")
    })
    @PutMapping("/{id}/read")
    public ResponseEntity<InAppNotificationResponse> markRead(@PathVariable("id") Long id) {
        var body = inAppNotificationQueryService.markRead(currentUserHelper.getId(), id);
        return ResponseEntity.ok(body);
    }
}
