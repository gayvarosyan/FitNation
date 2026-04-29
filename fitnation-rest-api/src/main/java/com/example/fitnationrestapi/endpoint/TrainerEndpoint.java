package com.example.fitnationrestapi.endpoint;

import com.example.fitnationbooking.service.GroupClassService;
import com.example.fitnationcommon.dto.request.CreateTrainerRequest;
import com.example.fitnationcommon.dto.request.EditTrainerRequest;
import com.example.fitnationcommon.dto.response.TrainerDirectoryItem;
import com.example.fitnationcommon.dto.response.TrainerStatsResponse;
import com.example.fitnationtrainer.service.TrainerManagementService;
import com.example.fitnationcommon.dto.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Trainers (admin)", description = "Trainer directory and lifecycle (ADMIN only)")
public class TrainerEndpoint {

    private final GroupClassService groupClassService;
    private final TrainerManagementService trainerManagementService;

    @Operation(summary = "Trainer statistics")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Stats returned"))
    @GetMapping("/stats")
    public TrainerStatsResponse getStats() {
        return trainerManagementService.getStats();
    }

    @Operation(
            summary = "Trainer directory (paged)",
            description = """
            Searchable, paginated trainer directory.

            **q** searches (case-insensitive, partial match):  
            • firstName  
            • lastName  
            • email  

            **sort** allowed fields: lastName, firstName, email, status, createdAt  
            Example: `lastName,asc`

            **size** max: 100 (default: 20)  
            **page** 0-based (default: 0)

            **status** optional filter: ACTIVE, PENDING, BLOCKED, DELETED
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page returned"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination params (bad sort field, negative page, size > 100)")
    })
    @GetMapping
    public PagedResponse<TrainerDirectoryItem> getDirectory(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "lastName,asc") String sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status) {

        return trainerManagementService.getDirectory(page, size, sort, q, status);
    }

    @Operation(summary = "Create trainer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping
    public TrainerDirectoryItem create(@Valid @RequestBody CreateTrainerRequest request) {
        return trainerManagementService.create(request);
    }

    @Operation(summary = "Update trainer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @PutMapping("/{id}")
    public TrainerDirectoryItem edit(
            @PathVariable Long id,
            @Valid @RequestBody EditTrainerRequest request) {
        return trainerManagementService.edit(id, request);
    }

    @Operation(summary = "Delete trainer", description = "Removes trainer and related group class data.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer deleted"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        groupClassService.deleteAllByTrainerId(id);
        trainerManagementService.delete(id);
    }
}
