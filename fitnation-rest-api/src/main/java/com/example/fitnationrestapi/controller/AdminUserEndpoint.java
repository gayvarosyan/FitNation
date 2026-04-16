package com.example.fitnationrestapi.controller;

import com.example.fitnationuser.service.UserAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin users", description = "Soft delete and restore users (ADMIN)")
public class AdminUserEndpoint {

    private final UserAdminService userAdminService;

    @Operation(summary = "Soft-delete user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userAdminService.softDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Restore user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User restored"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> restoreUser(@PathVariable Long id) {
        userAdminService.restoreUser(id);
        return ResponseEntity.noContent().build();
    }
}