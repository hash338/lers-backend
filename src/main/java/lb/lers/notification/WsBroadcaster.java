package lb.lers.notification;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WsBroadcaster {

    private final SimpMessagingTemplate template;

    public WsBroadcaster(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void broadcast(String topic, Object payload) {
        try {
            template.convertAndSend(topic, payload);
        } catch (Exception e) {
            System.out.println("WS broadcast error: " + e.getMessage());
        }
    }

    public void broadcastToCenter(String centerId, Object payload) {
        broadcast("/topic/center/" + centerId, payload);
    }

    public void broadcastIncident(String incidentId, Object payload) {
        broadcast("/topic/incidents/" + incidentId, payload);
    }
}