package demo.server.controller;

import demo.server.dto.CreateConversationRequest;
import demo.server.model.*;
import demo.server.repo.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/conversations")
public class ConversationController {
    private final ConversationRepo conversationRepo;
    private final ParticipantRepo participantRepo;
    private final UserRepo userRepo;

    public ConversationController(ConversationRepo conversationRepo, ParticipantRepo participantRepo, UserRepo userRepo) {
        this.conversationRepo = conversationRepo; this.participantRepo = participantRepo; this.userRepo = userRepo;
    }

    @PostMapping
    public Conversation create(@RequestBody CreateConversationRequest req){
        var conv = Conversation.builder()
                .type(Conversation.Type.valueOf(req.type()))
                .title(req.title())
                .createdAt(Instant.now())
                .build();
        conv = conversationRepo.save(conv);
        for(Long uid: req.participantUserIds()){
            participantRepo.save(Participant.builder()
                    .conversation(conv)
                    .user(userRepo.findById(uid).orElseThrow())
                    .role(Participant.Role.MEMBER)
                    .joinedAt(Instant.now())
                    .build());
        }
        return conv;
    }

    @GetMapping
    public java.util.List<Conversation> list(){
        return conversationRepo.findAll();
    }
}
