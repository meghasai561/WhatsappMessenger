package demo.server.controller;

import demo.server.dto.CreateConversationRequest;
import demo.server.model.Conversation;
import demo.server.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {
    private final ChatService chatService;

    @PostMapping
    public Conversation create(@RequestBody CreateConversationRequest req) {
        return chatService.createConversation(req.getType(), req.getParticipantIds());
    }
}
