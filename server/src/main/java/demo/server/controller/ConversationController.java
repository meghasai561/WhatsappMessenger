package demo.server.controller;

import demo.server.dto.CreateConversationRequest;
import demo.server.model.Conversation;
import demo.server.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations")
public class ConversationController {
    @Autowired
    private ChatService chatService;

    @PostMapping
    public Conversation create(@RequestBody CreateConversationRequest req) {
        return chatService.createConversation(req.getType(), req.getParticipantIds());
    }
}
