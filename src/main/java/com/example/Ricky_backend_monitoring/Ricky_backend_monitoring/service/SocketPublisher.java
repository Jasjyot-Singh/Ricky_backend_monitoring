package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.service;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.config.DashboardWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SocketPublisher {

    private final DashboardWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    public SocketPublisher(DashboardWebSocketHandler webSocketHandler, ObjectMapper objectMapper) {
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = objectMapper;
    }

    public void publish(String eventName, Object data) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("event", eventName);
            payload.put("data", data);
            String json = objectMapper.writeValueAsString(payload);
            webSocketHandler.broadcast(json);
        } catch (Exception e) {
            // Log serialization errors
        }
    }
}
