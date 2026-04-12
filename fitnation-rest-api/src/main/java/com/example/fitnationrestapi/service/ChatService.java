package com.example.fitnationrestapi.service;

import com.example.fitnationcommon.constants.ApplicationConstants;
import com.example.fitnationcommon.dto.request.OpenConversationRequest;
import com.example.fitnationcommon.dto.response.ConversationResponse;
import com.example.fitnationcommon.dto.response.MessageResponse;
import com.example.fitnationcommon.enums.UserRole;
import com.example.fitnationcommon.exception.ConversationNotFoundException;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatConversationRepository conversationRepo;
    private final ChatMessageRepository messageRepo;
    private final UserRepository userRepo;

    public ConversationResponse openOrGet(Long currentUserId, UserRole role,
                                          OpenConversationRequest req) {
        Long clientId, trainerId;

        if (role == UserRole.CLIENT) {
            clientId  = currentUserId;
            trainerId = Objects.requireNonNull(req.trainerId(), "trainerId is required");
        } else {
            trainerId = currentUserId;
            clientId  = Objects.requireNonNull(req.clientId(), "clientId is required");
        }

        ChatConversation conv = conversationRepo
                .findByClientIdAndTrainerId(clientId, trainerId)
                .orElseGet(() -> {
                    User client  = userRepo.getReferenceById(clientId);
                    User trainer = userRepo.getReferenceById(trainerId);
                    ChatConversation c = new ChatConversation();
                    c.setClient(client);
                    c.setTrainer(trainer);
                    return conversationRepo.save(c);
                });

        return toConvResponse(conv, null);
    }

    @Transactional(readOnly = true)
    public Page<ConversationResponse> listConversations(Long userId, Pageable pageable) {
        return conversationRepo.findAllByUserId(userId, pageable)
                .map(c -> {
                    Page<ChatMessage> last = messageRepo
                            .findByConversationIdOrderByCreatedAtDesc(c.getId(), PageRequest.of(0, 1));
                    MessageResponse lastMsg = last.isEmpty() ? null
                            : toMsgResponse(last.getContent().get(0));
                    return toConvResponse(c, lastMsg);
                });
    }

    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessages(Long userId, Long conversationId, Pageable pageable) {
        ChatConversation conv = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(
                        ApplicationConstants.MSG_CONVERSATION_NOT_FOUND + conversationId));
        assertParticipant(userId, conv);
        return messageRepo
                .findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(this::toMsgResponse);
    }

    public MessageResponse handleInbound(Long senderId, Long conversationId, String body) {
        if (body == null || body.isBlank() || body.length() > ApplicationConstants.CHAT_MESSAGE_MAX_LENGTH)
            throw new IllegalArgumentException("Invalid message body");

        ChatConversation conv = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(
                        ApplicationConstants.MSG_CONVERSATION_NOT_FOUND + conversationId));
        assertParticipant(senderId, conv);

        User sender = userRepo.getReferenceById(senderId);
        ChatMessage msg = new ChatMessage();
        msg.setConversation(conv);
        msg.setSender(sender);
        msg.setBody(body);
        ChatMessage saved = messageRepo.save(msg);

        conv.setLastMessageAt(saved.getCreatedAt());
        return toMsgResponse(saved);
    }

    private void assertParticipant(Long userId, ChatConversation conv) {
        boolean ok = conv.getClient().getId().equals(userId)
                || conv.getTrainer().getId().equals(userId);
        if (!ok) throw new AccessDeniedException(ApplicationConstants.MSG_NOT_CONVERSATION_PARTICIPANT);
    }

    private ConversationResponse toConvResponse(ChatConversation c, MessageResponse last) {
        return new ConversationResponse(c.getId(), c.getClient().getId(),
                c.getTrainer().getId(), c.getLastMessageAt(), last);
    }

    private MessageResponse toMsgResponse(ChatMessage m) {
        return new MessageResponse(m.getId(), m.getSender().getId(),
                m.getBody(), m.getCreatedAt());
    }
}