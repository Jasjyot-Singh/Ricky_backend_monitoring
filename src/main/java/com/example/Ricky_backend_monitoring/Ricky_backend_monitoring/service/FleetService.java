package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.service;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto.FleetStats;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.*;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FleetService {

    private final DeviceRepository deviceRepository;
    private final LiveStatusRepository liveStatusRepository;
    private final AlertRepository alertRepository;

    public FleetService(DeviceRepository deviceRepository,
                        LiveStatusRepository liveStatusRepository,
                        AlertRepository alertRepository) {
        this.deviceRepository = deviceRepository;
        this.liveStatusRepository = liveStatusRepository;
        this.alertRepository = alertRepository;
    }

    public List<Map<String, Object>> getFleetStatus(String search) {
        List<Device> devices = (search != null && !search.isEmpty()) ?
                deviceRepository.searchDevices(search) : deviceRepository.findAll();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Device dev : devices) {
            Optional<LiveStatus> statusOpt = liveStatusRepository.findById(dev.getDeviceId());
            Map<String, Object> map = new HashMap<>();
            map.put("deviceId", dev.getDeviceId());
            map.put("vehicleNumber", dev.getVehicleNumber());
            map.put("driverName", dev.getDriverName());
            map.put("firmwareVersion", dev.getFirmwareVersion());

            if (statusOpt.isPresent()) {
                LiveStatus ls = statusOpt.get();
                map.put("online", ls.getOnline());
                map.put("lastSeen", ls.getLastSeen());
                map.put("latitude", ls.getLatitude());
                map.put("longitude", ls.getLongitude());
                map.put("speed", ls.getSpeed());
                map.put("gpsFix", ls.getGpsFix());
                map.put("batteryPercentage", ls.getBatteryPercentage());
                map.put("batteryVoltage", ls.getBatteryVoltage());
                map.put("charging", ls.getCharging());
                map.put("powerSource", ls.getPowerSource());
                
                map.put("internetConnected", ls.getInternetConnected() != null ? ls.getInternetConnected() : false);
                map.put("system", Map.of(
                    "cpu", ls.getCpuUsage() != null ? ls.getCpuUsage() : 0,
                    "ram", ls.getRamUsage() != null ? ls.getRamUsage() : 0,
                    "disk", ls.getDiskUsage() != null ? ls.getDiskUsage() : 0,
                    "temp", ls.getCpuTemperature() != null ? ls.getCpuTemperature() : 0.0
                ));

                map.put("sosActive", ls.getSosActive());
                map.put("sosSource", ls.getSosSource());

                Map<String, Object> imuMap = new HashMap<>();
                imuMap.put("accelX", ls.getImuAccelX() != null ? ls.getImuAccelX() : 0.0);
                imuMap.put("accelY", ls.getImuAccelY() != null ? ls.getImuAccelY() : 0.0);
                imuMap.put("accelZ", ls.getImuAccelZ() != null ? ls.getImuAccelZ() : 0.0);
                imuMap.put("gyroX", ls.getImuGyroX() != null ? ls.getImuGyroX() : 0.0);
                imuMap.put("gyroY", ls.getImuGyroY() != null ? ls.getImuGyroY() : 0.0);
                imuMap.put("gyroZ", ls.getImuGyroZ() != null ? ls.getImuGyroZ() : 0.0);
                map.put("imu", imuMap);

                Map<String, Object> hwMap = new HashMap<>();
                hwMap.put("espConnected", ls.getEspConnected() != null ? ls.getEspConnected() : false);
                hwMap.put("gpsConnected", ls.getGpsConnected() != null ? ls.getGpsConnected() : false);
                hwMap.put("imuConnected", ls.getImuConnected() != null ? ls.getImuConnected() : false);
                hwMap.put("displayConnected", ls.getDisplayConnected() != null ? ls.getDisplayConnected() : false);
                map.put("hardware", hwMap);
            } else {
                map.put("online", false);
                map.put("lastSeen", null);
                map.put("latitude", null);
                map.put("longitude", null);
                map.put("speed", null);
                map.put("gpsFix", false);
                map.put("batteryPercentage", null);
                map.put("batteryVoltage", null);
                map.put("charging", false);
                map.put("powerSource", null);
                map.put("internetConnected", false);
                map.put("system", Map.of("cpu", 0, "ram", 0, "disk", 0, "temp", 0.0));
                map.put("sosActive", false);
                map.put("sosSource", null);
                map.put("imu", Map.of("accelX", 0.0, "accelY", 0.0, "accelZ", 0.0, "gyroX", 0.0, "gyroY", 0.0, "gyroZ", 0.0));
                map.put("hardware", Map.of("espConnected", false, "gpsConnected", false, "imuConnected", false, "displayConnected", false));
            }
            result.add(map);
        }
        return result;
    }

    public FleetStats getStats() {
        long totalDevices = deviceRepository.count();
        long online = liveStatusRepository.countByOnlineTrue();
        long offline = liveStatusRepository.countByOnlineFalse();
        
        // Correcting for unregistered status
        if (online + offline < totalDevices) {
            offline += (totalDevices - (online + offline));
        }

        long activeSOS = liveStatusRepository.countBySosActiveTrue();
        long lowBattery = liveStatusRepository.countLowBatteryDevices();

        return new FleetStats(totalDevices, online, offline, activeSOS, lowBattery);
    }

    public List<Alert> getActiveAlerts() {
        return alertRepository.findAllByResolvedFalseOrderByCreatedAtDesc();
    }
}
