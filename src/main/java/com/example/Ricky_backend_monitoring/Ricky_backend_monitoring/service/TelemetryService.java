package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.service;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto.TelemetryPayload;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto.HeartbeatPayload;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.*;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class TelemetryService {

    private final DeviceRepository deviceRepository;
    private final LiveStatusRepository liveStatusRepository;
    private final LocationHistoryRepository locationHistoryRepository;
    private final SosEventRepository sosEventRepository;
    private final AlertRepository alertRepository;
    private final TelemetryLogRepository telemetryLogRepository;
    private final SocketPublisher socketPublisher;
    private final ObjectMapper objectMapper;

    public TelemetryService(DeviceRepository deviceRepository,
                            LiveStatusRepository liveStatusRepository,
                            LocationHistoryRepository locationHistoryRepository,
                            SosEventRepository sosEventRepository,
                            AlertRepository alertRepository,
                            TelemetryLogRepository telemetryLogRepository,
                            SocketPublisher socketPublisher,
                            ObjectMapper objectMapper) {
        this.deviceRepository = deviceRepository;
        this.liveStatusRepository = liveStatusRepository;
        this.locationHistoryRepository = locationHistoryRepository;
        this.sosEventRepository = sosEventRepository;
        this.alertRepository = alertRepository;
        this.telemetryLogRepository = telemetryLogRepository;
        this.socketPublisher = socketPublisher;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void processTelemetry(TelemetryPayload payload) {
        LocalDateTime now = LocalDateTime.now();

        // 1. Ensure Device exists in Registry. If not, auto-register for ease of pilot deployment
        Optional<Device> devOpt = deviceRepository.findByDeviceId(payload.getDeviceId());
        Device device;
        if (devOpt.isEmpty()) {
            device = new Device(payload.getDeviceId(), "PILOT-" + payload.getDeviceId().replaceAll("[^0-9]", ""), "Pilot Driver", payload.getFirmwareVersion());
            deviceRepository.save(device);
        } else {
            device = devOpt.get();
            if (payload.getFirmwareVersion() != null && !payload.getFirmwareVersion().equals(device.getFirmwareVersion())) {
                device.setFirmwareVersion(payload.getFirmwareVersion());
                deviceRepository.save(device);
            }
        }

        // 2. Log Raw Telemetry
        try {
            String rawJson = objectMapper.writeValueAsString(payload);
            telemetryLogRepository.save(new TelemetryLog(payload.getDeviceId(), rawJson));
        } catch (Exception e) {
            // Ignore logging serialization errors
        }

        // 3. Retrieve or Create Live Status
        LiveStatus status = liveStatusRepository.findById(payload.getDeviceId())
                .orElseGet(() -> {
                    LiveStatus ls = new LiveStatus();
                    ls.setDeviceId(payload.getDeviceId());
                    return ls;
                });

        boolean previousOnline = Boolean.TRUE.equals(status.getOnline());
        boolean previousSosActive = Boolean.TRUE.equals(status.getSosActive());

        // Update basic metrics
        status.setOnline(true);
        status.setLastSeen(now);
        status.setFirmwareVersion(payload.getFirmwareVersion() != null ? payload.getFirmwareVersion() : device.getFirmwareVersion());
        status.setUpdatedAt(now);

        // Map GPS parameters
        if (payload.getGps() != null) {
            status.setLatitude(payload.getGps().getLat());
            status.setLongitude(payload.getGps().getLng());
            status.setSpeed(payload.getGps().getSpeed());
            status.setGpsFix(payload.getGps().getFixed());
        }

        if (payload.getHardware() != null) {
            status.setGpsConnected(payload.getHardware().getGpsConnected());
            status.setEspConnected(payload.getHardware().getEspConnected());
            status.setImuConnected(payload.getHardware().getImuConnected());
            status.setDisplayConnected(payload.getHardware().getDisplayConnected());
        }

        // Map Power/Battery parameters
        if (payload.getBattery() != null) {
            status.setBatteryPercentage(payload.getBattery().getPercentage());
            status.setBatteryVoltage(payload.getBattery().getVoltage());
            status.setCharging(payload.getBattery().getCharging());
            status.setPowerSource(payload.getBattery().getPowerSource());
        }

        // Map Internet parameters
        if (payload.getInternet() != null) {
            Object internetObj = payload.getInternet();
            if (internetObj instanceof Boolean) {
                status.setInternetConnected((Boolean) internetObj);
                status.setInternetSignalStrength(null);
            } else if (internetObj instanceof java.util.Map) {
                java.util.Map<?, ?> map = (java.util.Map<?, ?>) internetObj;
                Object conn = map.get("connected");
                if (conn instanceof Boolean) {
                    status.setInternetConnected((Boolean) conn);
                } else if (conn instanceof String) {
                    status.setInternetConnected(Boolean.parseBoolean((String) conn));
                }
                Object sig = map.get("signalStrength");
                if (sig instanceof Number) {
                    status.setInternetSignalStrength(((Number) sig).intValue());
                }
            }
        }

        // Map IMU parameters
        if (payload.getImu() != null) {
            status.setImuAccelX(payload.getImu().getAccelX());
            status.setImuAccelY(payload.getImu().getAccelY());
            status.setImuAccelZ(payload.getImu().getAccelZ());
            status.setImuGyroX(payload.getImu().getGyroX());
            status.setImuGyroY(payload.getImu().getGyroY());
            status.setImuGyroZ(payload.getImu().getGyroZ());
        }

        // Map Services parameters
        if (payload.getServices() != null) {
            status.setPosterBookingRunning(payload.getServices().getPosterBookingRunning());
            status.setTelemetryServiceRunning(payload.getServices().getTelemetryServiceRunning());
        }

        // Map System parameters
        if (payload.getSystem() != null) {
            status.setCpuUsage(payload.getSystem().getCpu());
            status.setRamUsage(payload.getSystem().getRam());
            status.setDiskUsage(payload.getSystem().getDisk());
            status.setCpuTemperature(payload.getSystem().getTemp());
        }

        // Map SOS parameters
        boolean currentSosActive = false;
        String currentSosSource = null;
        if (payload.getSos() != null) {
            currentSosActive = Boolean.TRUE.equals(payload.getSos().getActive());
            currentSosSource = payload.getSos().getSource();
        }
        status.setSosActive(currentSosActive);
        status.setSosSource(currentSosSource);

        liveStatusRepository.save(status);

        // 4. Save Location History (Breadcrumb for Route history)
        if (payload.getGps() != null && payload.getGps().getLat() != null && payload.getGps().getLng() != null) {
            locationHistoryRepository.save(new LocationHistory(
                    payload.getDeviceId(),
                    payload.getGps().getLat(),
                    payload.getGps().getLng(),
                    payload.getGps().getSpeed(),
                    now
            ));
            // Emit location update
            socketPublisher.publish("location-update", status);
        }

        // 5. Handle SOS Transitions
        if (currentSosActive && !previousSosActive) {
            // Transition FALSE -> TRUE: Create SOS Incident Event
            SosEvent sosEvent = new SosEvent(payload.getDeviceId(), currentSosSource != null ? currentSosSource : "unknown", now);
            sosEventRepository.save(sosEvent);

            // Create Alert
            createAlert(payload.getDeviceId(), "SOS", "Active SOS triggered from: " + sosEvent.getSource());
            
            // Broadcast WS SOS Alert event
            socketPublisher.publish("sos-alert", sosEvent);
        } else if (!currentSosActive && previousSosActive) {
            // Transition TRUE -> FALSE: Resolve SOS incident
            Optional<SosEvent> unresolvedOpt = sosEventRepository.findFirstByDeviceIdAndResolvedFalseOrderByTimestampDesc(payload.getDeviceId());
            if (unresolvedOpt.isPresent()) {
                SosEvent event = unresolvedOpt.get();
                event.setResolved(true);
                event.setResolvedAt(now);
                sosEventRepository.save(event);
            }
            resolveAlert(payload.getDeviceId(), "SOS");
        }

        // 6. Handle System Metrics & Hardware Alerts
        handleHardwareAlerts(payload);

        // 7. Handle Online Transition Alert
        if (!previousOnline) {
            resolveAlert(payload.getDeviceId(), "DEVICE_OFFLINE");
            socketPublisher.publish("device-online", status);
        }

        // 8. Publish Generic Telemetry Update
        socketPublisher.publish("telemetry-update", status);
    }

    @Transactional
    public void processHeartbeat(HeartbeatPayload payload) {
        LocalDateTime now = LocalDateTime.now();

        Optional<LiveStatus> statusOpt = liveStatusRepository.findById(payload.getDeviceId());
        if (statusOpt.isPresent()) {
            LiveStatus status = statusOpt.get();
            boolean previousOnline = Boolean.TRUE.equals(status.getOnline());
            status.setOnline(true);
            status.setLastSeen(now);
            status.setUpdatedAt(now);
            liveStatusRepository.save(status);

            if (!previousOnline) {
                resolveAlert(payload.getDeviceId(), "DEVICE_OFFLINE");
                socketPublisher.publish("device-online", status);
            }
        }
    }

    private void handleHardwareAlerts(TelemetryPayload payload) {
        String deviceId = payload.getDeviceId();

        // Battery level check
        if (payload.getBattery() != null && payload.getBattery().getPercentage() != null) {
            int pct = payload.getBattery().getPercentage();
            if (pct < 20) {
                createAlert(deviceId, "LOW_BATTERY", "Device battery percentage is critical: " + pct + "% (Voltage: " + payload.getBattery().getVoltage() + "V)");
            } else {
                resolveAlert(deviceId, "LOW_BATTERY");
            }
        }

        // GPS lock check
        if (payload.getGps() != null && payload.getGps().getFixed() != null) {
            if (Boolean.FALSE.equals(payload.getGps().getFixed())) {
                createAlert(deviceId, "GPS_FAILURE", "GPS Module lost satellite lock (fixed = false)");
            } else {
                resolveAlert(deviceId, "GPS_FAILURE");
            }
        }

        // Pocket WiFi/Internet connectivity check
        if (payload.getInternet() != null) {
            Boolean internetConnected = null;
            Object internetObj = payload.getInternet();
            if (internetObj instanceof Boolean) {
                internetConnected = (Boolean) internetObj;
            } else if (internetObj instanceof java.util.Map) {
                java.util.Map<?, ?> map = (java.util.Map<?, ?>) internetObj;
                Object conn = map.get("connected");
                if (conn instanceof Boolean) {
                    internetConnected = (Boolean) conn;
                }
            }
            if (Boolean.FALSE.equals(internetConnected)) {
                createAlert(deviceId, "INTERNET_FAILURE", "Internet connection is down on the Raspberry Pi gateway");
            } else if (Boolean.TRUE.equals(internetConnected)) {
                resolveAlert(deviceId, "INTERNET_FAILURE");
            }
        }

        // ESP communication link check
        if (payload.getHardware() != null && payload.getHardware().getEspConnected() != null) {
            if (Boolean.FALSE.equals(payload.getHardware().getEspConnected())) {
                createAlert(deviceId, "ESP_DISCONNECTED", "ESP32 Controller disconnected from Raspberry Pi UART line");
            } else {
                resolveAlert(deviceId, "ESP_DISCONNECTED");
            }
        }

        // Display check
        if (payload.getHardware() != null && payload.getHardware().getDisplayConnected() != null) {
            if (Boolean.FALSE.equals(payload.getHardware().getDisplayConnected())) {
                createAlert(deviceId, "DISPLAY_FAILURE", "Smart Signage Display disconnected or status flag is down");
            } else {
                resolveAlert(deviceId, "DISPLAY_FAILURE");
            }
        }

        // Poster ad booking running check
        if (payload.getServices() != null && payload.getServices().getPosterBookingRunning() != null) {
            if (Boolean.FALSE.equals(payload.getServices().getPosterBookingRunning())) {
                createAlert(deviceId, "POSTER_SERVICE_DOWN", "Smart Poster Booking service has stopped running");
            } else {
                resolveAlert(deviceId, "POSTER_SERVICE_DOWN");
            }
        }

        // Telemetry service check
        if (payload.getServices() != null && payload.getServices().getTelemetryServiceRunning() != null) {
            if (Boolean.FALSE.equals(payload.getServices().getTelemetryServiceRunning())) {
                createAlert(deviceId, "TELEMETRY_SERVICE_DOWN", "Device Telemetry Daemon service has stopped running");
            } else {
                resolveAlert(deviceId, "TELEMETRY_SERVICE_DOWN");
            }
        }
    }

    private void createAlert(String deviceId, String type, String message) {
        Optional<Alert> existing = alertRepository.findFirstByDeviceIdAndTypeAndResolvedFalseOrderByCreatedAtDesc(deviceId, type);
        if (existing.isEmpty()) {
            Alert alert = new Alert(deviceId, type, message);
            alertRepository.save(alert);
            socketPublisher.publish("alert-created", alert);
        }
    }

    private void resolveAlert(String deviceId, String type) {
        // No-op: Alerts must be resolved only and only manually by the operator
    }
}
