package demo.server.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Attachment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bucket;
    private String objectKey;
    private String mimeType;
    private Long sizeBytes;
    private Integer width;
    private Integer height;
    private Long durationMs;
    private String thumbKey;
    private Instant createdAt;
}
