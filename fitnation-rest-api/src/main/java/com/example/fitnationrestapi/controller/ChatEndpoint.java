package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.OpenConversationRequest;
import com.example.fitnationcommon.dto.response.ConversationResponse;
import com.example.fitnationcommon.dto.response.MessageResponse;
import com.example.fitnationrestapi.service.ChatService;
import com.example.fitnationrestapi.support.CurrentUserHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Tag(name = "Conversations", description = "Chat conversations and messages (CLIENT or TRAINER)")
public class ChatEndpoint {

    private final ChatService chatService;
    private final CurrentUserHelper currentUserHelper;

    @Operation(summary = "Open or get conversation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conversation returned"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<ConversationResponse> openConversation(
            @RequestBody OpenConversationRequest req) {

        return ResponseEntity.ok(chatService.openOrGet(
                currentUserHelper.getId(),
                currentUserHelper.getRole(),
                req));
    }

    @Operation(summary = "List conversations")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Page returned"))
    @GetMapping
    public ResponseEntity<Page<ConversationResponse>> listConversations(Pageable pageable) {

        return ResponseEntity.ok(
                chatService.listConversations(currentUserHelper.getId(), pageable));
    }

    @Operation(summary = "List messages in conversation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page returned"),
            @ApiResponse(responseCode = "403", description = "Not allowed for this conversation")
    })
    @GetMapping("/{id}/messages")
    public ResponseEntity<Page<MessageResponse>> getMessages(
            @PathVariable Long id,
            Pageable pageable) {

        return ResponseEntity.ok(
                chatService.getMessages(currentUserHelper.getId(), id, pageable));
    }
}