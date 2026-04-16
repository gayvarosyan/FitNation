package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.SendMessageRequest;
import com.example.fitnationcommon.dto.response.MessageResponse;
import com.example.fitnationrestapi.service.ChatService;
import com.example.fitnationrestapi.support.CurrentUserHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Tag(name = "Chat WebSocket")
public class ChatWebSocketEndpont {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final CurrentUserHelper currentUserHelper;

    @Operation(
            summary = "Send chat message (STOMP)",
            description = """
                    STOMP **SEND** destination: `/app/chat.send.{conversationId}` with JSON body `{"body":"..."}`.
                    Server broadcasts `MessageResponse` to subscribers of `/topic/chat.{conversationId}`.
                    Requires a prior STOMP **CONNECT** with JWT (see tag **Chat WebSocket**).
                    """)
    @MessageMapping("/chat.send.{conversationId}")
    public void sendMessage(
            @Parameter(description = "Conversation id", required = true)
            @DestinationVariable Long conversationId,
            @Payload @Valid SendMessageRequest request) {

        Long senderId = currentUserHelper.getId();
        MessageResponse response = chatService.handleInbound(senderId, conversationId, request.body());

        messagingTemplate.convertAndSend("/topic/chat." + conversationId, response);
    }
}