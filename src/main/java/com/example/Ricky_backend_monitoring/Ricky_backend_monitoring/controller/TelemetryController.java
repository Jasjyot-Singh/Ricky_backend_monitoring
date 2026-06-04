package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.controller;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto.HeartbeatPayload;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto.TelemetryPayload;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.service.TelemetryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/telemetry")
@CrossOrigin(origins = "*")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> receiveTelemetry(@RequestBody TelemetryPayload payload) {
        if (payload == null || payload.getDeviceId() == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "Missing deviceId"));
        }
        telemetryService.processTelemetry(payload);
        return ResponseEntity.ok(Map.of("success", true, "message", "Telemetry processed"));
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<Map<String, Object>> receiveHeartbeat(@RequestBody HeartbeatPayload payload) {
        if (payload == null || payload.getDeviceId() == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "Missing deviceId"));
        }
        telemetryService.processHeartbeat(payload);
        return ResponseEntity.ok(Map.of("success", true, "message", "Heartbeat received"));
    }
}
