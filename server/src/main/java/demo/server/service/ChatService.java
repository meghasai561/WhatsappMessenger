package demo.server.service;

import demo.server.model.*;
import demo.server.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ChatService {
    private final MessageRepo messageRepo;
    private final ConversationRepo conversationRepo;
    private final ParticipantRepo participantRepo;
    private final UserRepo userRepo;

    public ChatService(MessageRepo messageRepo, ConversationRepo conversationRepo, ParticipantRepo participantRepo, UserRepo userRepo) {
        this.messageRepo = messageRepo;
        this.conversationRepo = conversationRepo;
        this.participantRepo = participantRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public Message sendMessage(Long conversationId, Long senderId, String body, Message.ContentType contentType,
                               Long attachmentId, String clientMsgId, Long inReplyTo, Long originalId, boolean e2ee) {
        var conv = conversationRepo.findById(conversationId).orElseThrow();
        var sender = userRepo.findById(senderId).orElseThrow();
        var msg = Message.builder()
                .conversation(conv)
                .sender(sender)
                .body(body)
                .contentType(contentType)
                .attachmentId(attachmentId)
                .clientMsgId(clientMsgId)
                .inReplyToMessageId(inReplyTo)
                .originalMessageId(originalId)
                .e2ee(e2ee)
                .status(Message.Status.SENT)
                .sentAt(Instant.now())
                .build();
        return messageRepo.save(msg);
    }

    public List<Message> getMessages(Long conversationId) {
        return messageRepo.findByConversationIdOrderByIdAsc(conversationId);
    }
}
