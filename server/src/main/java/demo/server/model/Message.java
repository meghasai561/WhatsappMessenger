package demo.server.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
@Table(indexes = {@Index(columnList="conversation_id, id")})
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) private Conversation conversation;
    @ManyToOne(optional=false) private User sender;

    private String clientMsgId;
    private Long inReplyToMessageId;
    private Long originalMessageId;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(columnDefinition = "TEXT")
    private String body;

    private Long attachmentId;
    private boolean e2ee;

    private Instant sentAt;
    private Instant deliveredAt;
    private Instant readAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum ContentType { TEXT, IMAGE, VIDEO, AUDIO, SYSTEM }
    public enum Status { QUEUED, SENT, DELIVERED, READ }
}
