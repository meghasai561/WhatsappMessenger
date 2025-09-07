package demo.server.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
    private Long conversationId;
    private Long senderId;
    private String body;
    private String contentType; // text, image, video, audio
    private Long replyToMessageId;
    private Long forwardFromMessageId;
}