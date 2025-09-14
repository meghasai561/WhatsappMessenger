package demo.server.service;

import demo.server.model.*;
import demo.server.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import java.lang.*;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final UserRepository userRepo;
    private final ConversationRepository convRepo;
    private final ParticipantRepository partRepo;
    private final MessageRepository msgRepo;

    public User createUser(String username, String displayName) {
        User u = User.builder().username(username).displayName(displayName).build();
        return userRepo.save(u);
    }

    public Conversation createConversation(String type, List<Long> userIds) {
        Conversation conv = Conversation.builder().type(type).build();
        convRepo.save(conv);

        if (userIds == null || userIds.isEmpty()) {
            
            throw new IllegalArgumentException("participantIds must not be null or empty");

        }

        for (Long uid : userIds) {
            User u = userRepo.findById(uid).orElseThrow();
            Participant p = Participant.builder().conversation(conv).user(u).build();
            partRepo.save(p);
        }
        return conv;
    }

    public Message sendMessage(Long convId, Long senderId, String body, String contentType) {
        Conversation conv = convRepo.findById(convId).orElseThrow();
        User sender = userRepo.findById(senderId).orElseThrow();
        Message m = Message.builder()
                .conversation(conv)
                .sender(sender)
                .body(body)
                .contentType(contentType)
                .status("SENT")
                .sentAt(Instant.now())
                .build();
        return msgRepo.save(m);
    }

    public List<Conversation> getAllConversations() {
    return convRepo.findAll();
    }

    public List<Message> getMessagesByConversation(Long conversationId) {
        Conversation conversation = convRepo.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        return msgRepo.findByConversationOrderBySentAtAsc(conversation);
    }
}
