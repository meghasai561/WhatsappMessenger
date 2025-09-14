package demo.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne 
    @JsonBackReference
    private Conversation conversation;
    @ManyToOne private User sender;

    private String body;
    private String contentType;
    private Long attachmentId;

    private String status;
    private Instant sentAt;
    private Instant deliveredAt;
    private Instant readAt;
}
