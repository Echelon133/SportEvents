package ml.echelon133.sportevents.websocket;

import ml.echelon133.sportevents.event.types.AbstractMatchEvent;

public interface WebSocketEventService {
    void sendEventOverWebSocket(String websocketPath, AbstractMatchEvent event);
}
