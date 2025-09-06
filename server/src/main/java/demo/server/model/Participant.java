package demo.server.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"conversation_id","user_id"}))
public class Participant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) private Conversation conversation;
    @ManyToOne(optional=false) private User user;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Instant joinedAt;

    public enum Role { OWNER, ADMIN, MEMBER }
}
