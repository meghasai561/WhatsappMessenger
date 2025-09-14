package demo.server.repo;

import demo.server.model.Message;
import demo.server.model.Conversation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversation(Conversation conversation);
}
