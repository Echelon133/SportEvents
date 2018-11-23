package ml.echelon133.sportevents.websocket;

import ml.echelon133.sportevents.event.types.AbstractMatchEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketEventServiceImpl implements WebSocketEventService {

    private SimpMessagingTemplate template;

    @Autowired
    public WebSocketEventServiceImpl(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void sendEventOverWebSocket(String websocketPath, AbstractMatchEvent event) {
        this.template.convertAndSend(websocketPath, event);
    }
}
