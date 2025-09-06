package demo.server.controller;

import demo.server.dto.*;
import demo.server.model.Message;
import demo.server.service.ChatService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static demo.server.dto.Mappers.toDto;

@RestController
@RequestMapping("/conversations/{cid}/messages")
public class MessageController {
    private final ChatService chatService;
    private final SimpMessagingTemplate broker;

    public MessageController(ChatService chatService, SimpMessagingTemplate broker) {
        this.chatService = chatService; this.broker = broker;
    }

    @GetMapping
    public List<MessageDto> list(@PathVariable Long cid){
        return chatService.getMessages(cid).stream().map(Mappers::toDto).toList();
    }

    @PostMapping
    public MessageDto send(@PathVariable Long cid, @RequestParam Long senderUserId, @RequestBody SendMessageRequest req){
        var m = chatService.sendMessage(cid, senderUserId, req.body(),
                Message.ContentType.valueOf(req.contentType() == null ? "TEXT" : req.contentType()),
                req.attachmentId(), req.clientMsgId(), req.inReplyToMessageId(), req.originalMessageId(), Boolean.TRUE.equals(req.e2ee()));
        var dto = toDto(m);
        broker.convertAndSend("/topic/conversations."+cid, dto);
        return dto;
    }
}
