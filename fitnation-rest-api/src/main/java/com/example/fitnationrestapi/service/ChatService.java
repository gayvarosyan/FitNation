package com.example.fitnationrestapi.service;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.request.OpenConversationRequest;
import com.example.fitnationcommon.dto.response.ConversationResponse;
import com.example.fitnationcommon.dto.response.MessageResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.ConversationNotFoundException;
import com.example.fitnationcommon.exception.UserDeletedException;
import com.example.fitnationrestapi.entity.ChatConversation;
import com.example.fitnationrestapi.entity.ChatMessage;
import com.example.fitnationrestapi.repository.ChatConversationRepository;
import com.example.fitnationrestapi.repository.ChatMessageRepository;
import com.example.fitnationuser.repository.UserRepository;
import com.example.fitnationuser.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService extends PerformanceOptimizedService {

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository      messageRepository;
    private final UserRepository             userRepo;

    public ConversationResponse openOrGet(Long currentUserId, UserRole role,
                                          OpenConversationRequest req) {
        assertUserActive(currentUserId);

        Long clientId, trainerId;

        if (role == UserRole.CLIENT) {
            clientId  = currentUserId;
            trainerId = Objects.requireNonNull(req.trainerId(), "trainerId is required");
        } else {
            trainerId = currentUserId;
            clientId  = Objects.requireNonNull(req.clientId(), "clientId is required");
        }

        ChatConversation conversation = conversationRepository
                .findByClientIdAndTrainerId(clientId, trainerId)
                .orElseGet(() -> {
                    User client  = userRepo.getReferenceById(clientId);
                    User trainer = userRepo.getReferenceById(trainerId);
                    ChatConversation newConversation = new ChatConversation();
                    newConversation.setClient(client);
                    newConversation.setTrainer(trainer);
                    return conversationRepository.save(newConversation);
                });

        return toConvResponse(conversation, null);
    }

    @Transactional(readOnly = true)
    public Page<ConversationResponse> listConversations(Long userId, Pageable pageable) {
        assertUserActive(userId);

        return executeReadOnlyWithMonitoring("listConversations", () -> {
            Pageable validatedPageable = validatePageable(pageable);
            Page<ConversationResponse> result = conversationRepository
                    .findAllByUserIdWithUsers(userId, validatedPageable)
                    .map(conversation -> {
                        Page<ChatMessage> last = messageRepository
                                .findByConversationIdOrderByCreatedAtDesc(
                                        conversation.getId(), PageRequest.of(0, 1));
                        MessageResponse lastMsg = last.isEmpty() ? null
                                : toMsgResponse(last.getContent().get(0));
                        return toConvResponse(conversation, lastMsg);
                    });
            logPaginationInfo("listConversations", result);
            return result;
        });
    }

    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessages(Long userId, Long conversationId, Pageable pageable) {
        assertUserActive(userId);

        return executeReadOnlyWithMonitoring("getMessages", () -> {
            Pageable validatedPageable = validatePageable(pageable);
            ChatConversation conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new ConversationNotFoundException(
                            ApplicationConstants.MSG_CONVERSATION_NOT_FOUND + conversationId));
            assertParticipant(userId, conversation);
            Page<MessageResponse> result = messageRepository
                    .findByConversationIdOrderByCreatedAtDescWithSender(conversationId, validatedPageable)
                    .map(this::toMsgResponse);
            logPaginationInfo("getMessages", result);
            return result;
        });
    }

    public MessageResponse handleInbound(Long senderId, Long conversationId, String body) {
        if (body == null || body.isBlank() || body.length() > ApplicationConstants.CHAT_MESSAGE_MAX_LENGTH)
            throw new IllegalArgumentException("Invalid message body");

        assertUserActive(senderId);

        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(
                        ApplicationConstants.MSG_CONVERSATION_NOT_FOUND + conversationId));
        assertParticipant(senderId, conversation);

        User sender = userRepo.getReferenceById(senderId);
        ChatMessage msg = new ChatMessage();
        msg.setConversation(conversation);
        msg.setSender(sender);
        msg.setBody(body);
        ChatMessage saved = messageRepository.save(msg);

        conversation.setLastMessageAt(saved.getCreatedAt());
        return toMsgResponse(saved);
    }

    private void assertUserActive(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ApplicationConstants.MSG_USER_NOT_FOUND + userId));
        if (user.getDeletedAt() != null) {
            throw new UserDeletedException(userId);
        }
    }

    private void assertParticipant(Long userId, ChatConversation conversation) {
        boolean ok = conversation.getClient().getId().equals(userId)
                || conversation.getTrainer().getId().equals(userId);
        if (!ok) throw new AccessDeniedException(ApplicationConstants.MSG_NOT_CONVERSATION_PARTICIPANT);
    }

    private ConversationResponse toConvResponse(ChatConversation conversation, MessageResponse last) {
        return new ConversationResponse(conversation.getId(), conversation.getClient().getId(),
                conversation.getTrainer().getId(), conversation.getLastMessageAt(), last);
    }

    private MessageResponse toMsgResponse(ChatMessage m) {
        return new MessageResponse(m.getId(), m.getSender().getId(),
                m.getBody(), m.getCreatedAt());
    }
}