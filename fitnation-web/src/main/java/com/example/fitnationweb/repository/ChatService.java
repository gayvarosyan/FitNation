package com.example.fitnationweb.repository;

import com.example.fitnationcommon.dto.request.OpenConversationRequest;
import com.example.fitnationcommon.dto.response.ConversationResponse;
import com.example.fitnationcommon.dto.response.MessageResponse;
import com.example.fitnationcommon.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {

    ConversationResponse openOrGet(Long currentUserId, UserRole role, OpenConversationRequest req);

    Page<ConversationResponse> listConversations(Long userId, Pageable pageable);

    Page<MessageResponse> getMessages(Long userId, Long conversationId, Pageable pageable);
}

