package demo.server.controller;

import demo.server.dto.MessageDto;
import demo.server.dto.SendMessageRequest;
import demo.server.dto.Mappers;
import demo.server.model.Message;
import demo.server.service.ChatService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    private ChatService chatService;

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
}
