package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.service;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.*;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final LiveStatusRepository liveStatusRepository;
    private final LocationHistoryRepository locationHistoryRepository;
    private final SosEventRepository sosEventRepository;
    private final TelemetryLogRepository telemetryLogRepository;
    private final ObjectMapper objectMapper;

    public DeviceService(DeviceRepository deviceRepository,
                         LiveStatusRepository liveStatusRepository,
                         LocationHistoryRepository locationHistoryRepository,
                         SosEventRepository sosEventRepository,
                         TelemetryLogRepository telemetryLogRepository,
                         ObjectMapper objectMapper) {
        this.deviceRepository = deviceRepository;
        this.liveStatusRepository = liveStatusRepository;
        this.locationHistoryRepository = locationHistoryRepository;
        this.sosEventRepository = sosEventRepository;
        this.telemetryLogRepository = telemetryLogRepository;
        this.objectMapper = objectMapper;
    }

    public Optional<Map<String, Object>> getDeviceDetails(String deviceId) {
        Optional<Device> devOpt = deviceRepository.findByDeviceId(deviceId);
        if (devOpt.isEmpty()) {
            return Optional.empty();
        }
        Device dev = devOpt.get();
        Optional<LiveStatus> lsOpt = liveStatusRepository.findById(deviceId);

        Map<String, Object> response = new HashMap<>();
        response.put("deviceId", dev.getDeviceId());
        response.put("vehicleNumber", dev.getVehicleNumber());
        response.put("driverName", dev.getDriverName());
        response.put("createdAt", dev.getCreatedAt());
        response.put("liveStatus", lsOpt.orElse(null));

        return Optional.of(response);
    }

    public List<LocationHistory> getRouteHistory(String deviceId, LocalDateTime fromDate, LocalDateTime toDate) {
        return locationHistoryRepository.findRouteHistory(deviceId, fromDate, toDate);
    }

    public List<SosEvent> getSosHistory(String deviceId) {
        return sosEventRepository.findAllByDeviceIdOrderByTimestampDesc(deviceId);
    }

    public List<Map<String, Object>> getTelemetryHistory(String deviceId) {
        // Fetch last 50 telemetry logs and parse for charts
        Pageable limit = PageRequest.of(0, 50);
        List<TelemetryLog> logs = telemetryLogRepository.findLatestLogs(deviceId, limit);

        List<Map<String, Object>> history = new ArrayList<>();
        // Reverse so chronological order
        Collections.reverse(logs);

        for (TelemetryLog log : logs) {
            try {
                JsonNode root = objectMapper.readTree(log.getPayloadJson());
                Map<String, Object> entry = new HashMap<>();
                
                String timestampStr = root.path("timestamp").asText();
                entry.put("timestamp", (timestampStr.isEmpty() || "null".equals(timestampStr)) 
                        ? log.getReceivedAt().toString() 
                        : timestampStr);
                
                entry.put("batteryPercentage", root.path("battery").path("percentage").asInt());
                entry.put("cpuUsage", root.path("system").path("cpu").asInt());
                entry.put("cpuTemperature", root.path("system").path("temp").asDouble());
                
                history.add(entry);
            } catch (Exception e) {
                // Skip malformed payloads
            }
        }
        return history;
    }

    public Optional<Map<String, Object>> getHealth(String deviceId) {
        Optional<LiveStatus> lsOpt = liveStatusRepository.findById(deviceId);
        if (lsOpt.isEmpty()) {
            return Optional.empty();
        }
        LiveStatus ls = lsOpt.get();

        Map<String, Object> map = new HashMap<>();
        map.put("deviceId", ls.getDeviceId());
        map.put("online", ls.getOnline());
        map.put("internetConnected", ls.getInternetConnected());
        
        Map<String, Object> hardware = new HashMap<>();
        hardware.put("espConnected", ls.getEspConnected());
        hardware.put("gpsConnected", ls.getGpsConnected());
        hardware.put("imuConnected", ls.getImuConnected());
        hardware.put("displayConnected", ls.getDisplayConnected());
        map.put("hardware", hardware);

        Map<String, Object> services = new HashMap<>();
        services.put("posterBookingRunning", ls.getPosterBookingRunning());
        services.put("telemetryServiceRunning", ls.getTelemetryServiceRunning());
        map.put("services", services);

        return Optional.of(map);
    }
}
