package demo.server.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class MessageDto {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String body;
    private String contentType;
    private Long attachmentId;
    private String status;
    private Instant sentAt;
    private Instant deliveredAt;
    private Instant readAt;
}
