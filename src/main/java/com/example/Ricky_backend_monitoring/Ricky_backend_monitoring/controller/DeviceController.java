package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.controller;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto.CommandRequest;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.DeviceCommand;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.LocationHistory;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.SosEvent;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.service.CommandService;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.service.DeviceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    private final DeviceService deviceService;
    private final CommandService commandService;

    public DeviceController(DeviceService deviceService, CommandService commandService) {
        this.deviceService = deviceService;
        this.commandService = commandService;
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<Map<String, Object>> getDevice(@PathVariable("deviceId") String deviceId) {
        return deviceService.getDeviceDetails(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{deviceId}/routes")
    public ResponseEntity<List<Map<String, Object>>> getRoute(
            @PathVariable("deviceId") String deviceId,
            @RequestParam(value = "fromDate", required = false) String fromDateStr,
            @RequestParam(value = "toDate", required = false) String toDateStr,
            @RequestParam(value = "from", required = false) String fromStr,
            @RequestParam(value = "to", required = false) String toStr) {

        String finalFrom = fromDateStr != null ? fromDateStr : fromStr;
        String finalTo = toDateStr != null ? toDateStr : toStr;

        LocalDateTime fromDate;
        LocalDateTime toDate;
        try {
            if (finalFrom != null) {
                fromDate = LocalDateTime.parse(finalFrom, DateTimeFormatter.ISO_DATE_TIME);
            } else {
                fromDate = LocalDateTime.now().minusDays(1);
            }
            if (finalTo != null) {
                toDate = LocalDateTime.parse(finalTo, DateTimeFormatter.ISO_DATE_TIME);
            } else {
                toDate = LocalDateTime.now();
            }
        } catch (Exception e) {
            fromDate = LocalDateTime.now().minusDays(1);
            toDate = LocalDateTime.now();
        }

        List<LocationHistory> history = deviceService.getRouteHistory(deviceId, fromDate, toDate);
        List<Map<String, Object>> response = history.stream().map(h -> {
            Map<String, Object> item = new java.util.LinkedHashMap<>();
            item.put("latitude", h.getLatitude());
            item.put("longitude", h.getLongitude());
            item.put("speed", h.getSpeed());
            item.put("timestamp", h.getTimestamp());
            return item;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{deviceId}/sos-history")
    public ResponseEntity<List<SosEvent>> getSosHistory(@PathVariable("deviceId") String deviceId) {
        return ResponseEntity.ok(deviceService.getSosHistory(deviceId));
    }

    @GetMapping("/{deviceId}/telemetry-history")
    public ResponseEntity<List<Map<String, Object>>> getTelemetryHistory(@PathVariable("deviceId") String deviceId) {
        return ResponseEntity.ok(deviceService.getTelemetryHistory(deviceId));
    }

    @GetMapping("/{deviceId}/telemetry-logs")
    public ResponseEntity<List<Map<String, Object>>> getTelemetryLogs(
            @PathVariable("deviceId") String deviceId,
            @RequestParam("fromDate") String fromDateStr,
            @RequestParam("toDate") String toDateStr) {
        
        LocalDateTime fromDate = LocalDateTime.parse(fromDateStr, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime toDate = LocalDateTime.parse(toDateStr, DateTimeFormatter.ISO_DATE_TIME);
        return ResponseEntity.ok(deviceService.getTelemetryLogsForDate(deviceId, fromDate, toDate));
    }

    @GetMapping("/{deviceId}/health")
    public ResponseEntity<Map<String, Object>> getHealth(@PathVariable("deviceId") String deviceId) {
        return deviceService.getHealth(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Command APIs
    @PostMapping("/{deviceId}/commands")
    public ResponseEntity<Map<String, Object>> sendCommand(
            @PathVariable("deviceId") String deviceId,
            @RequestBody CommandRequest request) {
        if (request == null || request.getCommand() == null) {
            return ResponseEntity.badRequest().build();
        }
        DeviceCommand cmd = commandService.queueCommand(deviceId, request.getCommand());
        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("success", true);
        response.put("commandId", cmd.getId());
        response.put("status", cmd.getStatus());
        response.put("message", "Command queued successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{deviceId}/commands")
    public ResponseEntity<List<Map<String, Object>>> getCommandHistory(@PathVariable("deviceId") String deviceId) {
        List<DeviceCommand> history = commandService.getCommandHistory(deviceId);
        List<Map<String, Object>> response = history.stream().map(cmd -> {
            Map<String, Object> item = new java.util.LinkedHashMap<>();
            item.put("id", cmd.getId());
            item.put("command", cmd.getCommand());
            item.put("status", cmd.getStatus());
            item.put("createdAt", cmd.getCreatedAt());
            item.put("executedAt", cmd.getExecutedAt());
            item.put("response", cmd.getResponse());
            return item;
        }).toList();
        return ResponseEntity.ok(response);
    }

    // Device checks for pending commands (polling)
    @GetMapping("/{deviceId}/commands/pending")
    public ResponseEntity<List<DeviceCommand>> getPendingCommands(@PathVariable("deviceId") String deviceId) {
        return ResponseEntity.ok(commandService.getPendingCommands(deviceId));
    }

    // Device responds to a command (execution confirmation)
    @PostMapping("/{deviceId}/commands/{commandId}/respond")
    public ResponseEntity<Map<String, Object>> respondToCommand(
            @PathVariable("deviceId") String deviceId,
            @PathVariable("commandId") Long commandId,
            @RequestBody Map<String, String> responseBody) {

        String status = responseBody.getOrDefault("status", "executed");
        String responseText = responseBody.getOrDefault("response", "");

        Optional<DeviceCommand> updated = commandService.respondToCommand(deviceId, commandId, status, responseText);
        if (updated.isPresent()) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Command status updated"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "error", "Command not found or device mismatch"));
    }
}
