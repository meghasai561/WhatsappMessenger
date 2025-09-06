package demo.server.dto;

import demo.server.model.Message;
import java.time.Instant;
import java.util.List;

public record SendMessageRequest(
        String clientMsgId,
        String body,
        String contentType, // TEXT|IMAGE|VIDEO|AUDIO
        Long attachmentId,
        Long inReplyToMessageId,
        Long originalMessageId,
        Boolean e2ee
) {}

public record MessageDto(
        Long id, Long conversationId, Long senderUserId,
        String body, String contentType, Long attachmentId,
        String status, Instant sentAt, Instant deliveredAt, Instant readAt,
        String clientMsgId, Long originalMessageId, Long inReplyToMessageId
){}

public record CreateConversationRequest(String type, String title, List<Long> participantUserIds) {}

public record CreateUserRequest(String username, String displayName) {}

public record UserDto(Long id, String username, String displayName) {}

public class Mappers {
    public static MessageDto toDto(Message m) {
        return new MessageDto(
                m.getId(),
                m.getConversation().getId(),
                m.getSender().getId(),
                m.getBody(),
                m.getContentType().name(),
                m.getAttachmentId(),
                m.getStatus().name(),
                m.getSentAt(),
                m.getDeliveredAt(),
                m.getReadAt(),
                m.getClientMsgId(),
                m.getOriginalMessageId(),
                m.getInReplyToMessageId()
        );
    }
}
