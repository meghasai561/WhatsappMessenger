package demo.server.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateConversationRequest {
    private String type; // DIRECT or GROUP
    private List<Long> participantIds;
}
