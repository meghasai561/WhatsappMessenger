package demo.server.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PresenceHub {
    private final SimpMessagingTemplate broker;
    private final Set<String> online = ConcurrentHashMap.newKeySet();
    public PresenceHub(SimpMessagingTemplate broker){ this.broker = broker; }
    public void online(String username){ online.add(username); broker.convertAndSend("/topic/presence","ONLINE:"+username); }
    public void offline(String username){ online.remove(username); broker.convertAndSend("/topic/presence","OFFLINE:"+username); }
    public boolean isOnline(String username){ return online.contains(username); }
}
