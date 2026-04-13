package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.request.SendMessageRequest;
import com.example.fitnationcommon.dto.response.MessageResponse;
import com.example.fitnationrestapi.service.ChatService;
import com.example.fitnationrestapi.support.CurrentUserHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketEndpont {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final CurrentUserHelper currentUserHelper;

    @MessageMapping("/chat.send.{conversationId}")
    public void sendMessage(@DestinationVariable Long conversationId,
                            @Payload @Valid SendMessageRequest request) {

        Long senderId = currentUserHelper.getId();
        MessageResponse response = chatService.handleInbound(senderId, conversationId, request.body());

        messagingTemplate.convertAndSend("/topic/chat." + conversationId, response);
    }
}