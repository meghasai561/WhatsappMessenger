package demo.server.dto;

import demo.server.model.Message;
import demo.server.model.User;

public class Mappers {
    public static MessageDto toDto(Message m) {
        MessageDto dto = new MessageDto();
        dto.setId(m.getId());
        dto.setConversationId(m.getConversation().getId());
        dto.setSenderId(m.getSender().getId());
        dto.setBody(m.getBody());
        dto.setContentType(m.getContentType());
        dto.setAttachmentId(m.getAttachmentId());
        dto.setStatus(m.getStatus());
        dto.setSentAt(m.getSentAt());
        dto.setDeliveredAt(m.getDeliveredAt());
        dto.setReadAt(m.getReadAt());
        return dto;
    }

    public static UserDto toDto(User u) {
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setDisplayName(u.getDisplayName());
        return dto;
    }
}
