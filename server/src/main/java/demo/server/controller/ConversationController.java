package demo.server.controller;

import demo.server.dto.CreateConversationRequest;
import demo.server.model.Conversation;
import demo.server.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import demo.server.model.Message;
import demo.server.dto.SendMessageRequest;
import demo.server.repo.ConversationRepository;

@RestController
@RequestMapping("/conversations")
public class ConversationController {
    @Autowired
    private ChatService chatService;

    @PostMapping
    public Conversation create(@RequestBody CreateConversationRequest req) {
        return chatService.createConversation(req.getType(), req.getParticipantIds());
    }

    @GetMapping
    public List<Conversation> getAllConversations() {
        return chatService.getAllConversations();
    }

    @PostMapping("/{conversationId}/messages")
    public Message sendMessage(
            @PathVariable Long conversationId,
            @RequestParam Long senderUserId,
            @RequestBody SendMessageRequest req
    ) {
        return chatService.sendMessage(conversationId, senderUserId, req.getBody(), req.getContentType());
    }

    @GetMapping("/{conversationId}/messages")
    public List<Message> getMessages(
            @PathVariable Long conversationId
    ) {
        return chatService.getMessagesByConversation(conversationId);
    }
}
