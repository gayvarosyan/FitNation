package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.OpenConversationRequest;
import com.example.fitnationcommon.dto.response.ConversationResponse;
import com.example.fitnationcommon.dto.response.MessageResponse;
import com.example.fitnationrestapi.service.ChatService;
import com.example.fitnationrestapi.support.CurrentUserHelper;
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
public class ChatEndpoint {

    private final ChatService chatService;
    private final CurrentUserHelper currentUserHelper;

    @PostMapping
    public ResponseEntity<ConversationResponse> openConversation(
            @RequestBody OpenConversationRequest req) {

        return ResponseEntity.ok(chatService.openOrGet(
                currentUserHelper.getId(),
                currentUserHelper.getRole(),
                req));
    }

    @GetMapping
    public ResponseEntity<Page<ConversationResponse>> listConversations(Pageable pageable) {

        return ResponseEntity.ok(
                chatService.listConversations(currentUserHelper.getId(), pageable));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<Page<MessageResponse>> getMessages(
            @PathVariable Long id,
            Pageable pageable) {

        return ResponseEntity.ok(
                chatService.getMessages(currentUserHelper.getId(), id, pageable));
    }
}