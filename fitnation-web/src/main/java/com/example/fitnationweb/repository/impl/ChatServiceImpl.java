package com.example.fitnationweb.repository.impl;

import com.example.fitnationcommon.dto.request.OpenConversationRequest;
import com.example.fitnationcommon.dto.response.ConversationResponse;
import com.example.fitnationcommon.dto.response.MessageResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationweb.repository.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    @Override
    public ConversationResponse openOrGet(Long currentUserId, UserRole role,
                                          OpenConversationRequest req) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<ConversationResponse> listConversations(Long userId, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Page<MessageResponse> getMessages(Long userId, Long conversationId,
                                             Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

