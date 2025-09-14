package demo.server.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // DIRECT or GROUP

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private List<Participant> participants;

    @OneToMany(mappedBy = "conversation")
    @JsonManagedReference // Paired with JsonBackReference
    private List<Message> messages;
}
