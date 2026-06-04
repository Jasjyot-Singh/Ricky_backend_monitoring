package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.controller;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.Alert;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<List<Alert>> getAlertsByDevice(@PathVariable("deviceId") String deviceId) {
        return ResponseEntity.ok(alertService.getAlertsByDevice(deviceId));
    }

    @PatchMapping("/{alertId}/resolve")
    public ResponseEntity<Map<String, Object>> resolveAlert(@PathVariable("alertId") Long alertId) {
        return alertService.resolveAlert(alertId)
                .map(alert -> ResponseEntity.ok(Map.<String, Object>of(
                        "success", true,
                        "alertId", alert.getId(),
                        "resolved", true,
                        "resolvedAt", alert.getResolvedAt()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}
