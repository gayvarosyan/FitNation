package com.example.fitnationrestapi.endpoint;

import com.example.fitnationprogress.dto.ProgressEntryResponse;
import com.example.fitnationprogress.dto.ProgressSummaryResponse;
import com.example.fitnationprogress.service.UserProgressService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainers/clients/{userId}/progress")
@PreAuthorize("hasRole('TRAINER')")
@Tag(name = "Trainer client progress", description = "Trainer visibility into assigned client progress")
public class TrainerClientProgressEndpoint {

    private final UserProgressService userProgressService;
    private final CurrentUserHelper currentUserHelper;

    @Operation(summary = "Get client progress history")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "History returned"),
            @ApiResponse(responseCode = "403", description = "Not assigned trainer")
    })
    @GetMapping
    public Page<ProgressEntryResponse> history(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "recordedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return userProgressService.getUserHistoryByActor(
                currentUserHelper.getId(),
                currentUserHelper.getRole(),
                userId,
                pageable);
    }

    @Operation(summary = "Get client summary and trends")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Summary returned"),
            @ApiResponse(responseCode = "403", description = "Not assigned trainer")
    })
    @GetMapping("/summary")
    public ProgressSummaryResponse summary(@PathVariable Long userId) {
        return userProgressService.getSummaryByActor(
                currentUserHelper.getId(),
                currentUserHelper.getRole(),
                userId);
    }
}
