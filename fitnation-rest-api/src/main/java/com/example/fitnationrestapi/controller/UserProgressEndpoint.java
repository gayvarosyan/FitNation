package com.example.fitnationrestapi.controller;

import com.example.fitnationprogress.dto.ProgressEntryResponse;
import com.example.fitnationprogress.dto.ProgressSummaryResponse;
import com.example.fitnationprogress.dto.UpsertUserProgressEntryRequest;
import com.example.fitnationprogress.service.UserProgressService;
import com.example.fitnationrestapi.support.CurrentUserHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/progress")
@PreAuthorize("hasRole('CLIENT')")
@Tag(name = "User progress", description = "Personal progress tracking endpoints")
public class UserProgressEndpoint {

    private final UserProgressService userProgressService;
    private final CurrentUserHelper currentUserHelper;

    @Operation(summary = "Create a new progress entry")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entry created"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<ProgressEntryResponse> create(@Valid @RequestBody UpsertUserProgressEntryRequest request) {
        var response = userProgressService.createEntry(currentUserHelper.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get my progress history")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "History returned"))
    @GetMapping
    public Page<ProgressEntryResponse> history(
            @PageableDefault(size = 20, sort = "recordedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return userProgressService.getMyHistory(currentUserHelper.getId(), pageable);
    }

    @Operation(summary = "Get a specific progress entry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entry returned"),
            @ApiResponse(responseCode = "404", description = "Entry not found")
    })
    @GetMapping("/{id}")
    public ProgressEntryResponse byId(@PathVariable Long id) {
        return userProgressService.getMyEntry(currentUserHelper.getId(), id);
    }

    @Operation(summary = "Update a progress entry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entry updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "Entry not found")
    })
    @PutMapping("/{id}")
    public ProgressEntryResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpsertUserProgressEntryRequest request) {
        return userProgressService.updateMyEntry(currentUserHelper.getId(), id, request);
    }

    @Operation(summary = "Delete my progress entry")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Entry deleted"),
            @ApiResponse(responseCode = "404", description = "Entry not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userProgressService.deleteMyEntry(currentUserHelper.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get progress summary and trends")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Summary returned"))
    @GetMapping("/summary")
    public ProgressSummaryResponse summary() {
        return userProgressService.getSummary(currentUserHelper.getId());
    }
}
