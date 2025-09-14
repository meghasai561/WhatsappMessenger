package demo.server.controller;

import demo.server.dto.MessageDto;
import demo.server.dto.SendMessageRequest;
import demo.server.dto.Mappers;
import demo.server.model.Message;
import demo.server.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import demo.server.model.Conversation;
import demo.server.repo.ConversationRepository;
import demo.server.repo.MessageRepository;
import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final ChatService chatService;

    @PostMapping
    public MessageDto send(@RequestBody SendMessageRequest req) {
        Message m = chatService.sendMessage(
                req.getConversationId(),
                req.getSenderId(),
                req.getBody(),
                req.getContentType()
        );
        return Mappers.toDto(m);
    }
    @GetMapping("/conversation/{conversationId}")
    public List<Message> getMessagesByConversation(@PathVariable Long conversationId) {
        Conversation conversation = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        return messageRepo.findByConversationOrderByTimestampAsc(conversation);
    }
}
