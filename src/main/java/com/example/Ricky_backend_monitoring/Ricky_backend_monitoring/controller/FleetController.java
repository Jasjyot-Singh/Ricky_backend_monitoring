package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.controller;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto.FleetStats;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.Alert;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.service.FleetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fleet")
@CrossOrigin(origins = "*")
public class FleetController {

    private final FleetService fleetService;

    public FleetController(FleetService fleetService) {
        this.fleetService = fleetService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getStatus(@RequestParam(value = "search", required = false) String search) {
        return ResponseEntity.ok(fleetService.getFleetStatus(search));
    }

    @GetMapping("/stats")
    public ResponseEntity<FleetStats> getStats() {
        return ResponseEntity.ok(fleetService.getStats());
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Alert>> getActiveAlerts() {
        return ResponseEntity.ok(fleetService.getActiveAlerts());
    }
}
