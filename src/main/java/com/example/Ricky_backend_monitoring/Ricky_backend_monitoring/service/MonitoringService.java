package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.service;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.Alert;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.LiveStatus;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository.AlertRepository;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository.LiveStatusRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MonitoringService {

    private final LiveStatusRepository liveStatusRepository;
    private final AlertRepository alertRepository;
    private final SocketPublisher socketPublisher;

    public MonitoringService(LiveStatusRepository liveStatusRepository,
                             AlertRepository alertRepository,
                             SocketPublisher socketPublisher) {
        this.liveStatusRepository = liveStatusRepository;
        this.alertRepository = alertRepository;
        this.socketPublisher = socketPublisher;
    }

    // @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkDeviceHeartbeats() {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(60);
        List<LiveStatus> allDevices = liveStatusRepository.findAll();

        for (LiveStatus status : allDevices) {
            if (Boolean.TRUE.equals(status.getOnline())) {
                LocalDateTime lastSeen = status.getLastSeen();
                if (lastSeen == null || lastSeen.isBefore(cutoff)) {
                    // Mark as Offline
                    status.setOnline(false);
                    status.setUpdatedAt(LocalDateTime.now());
                    liveStatusRepository.save(status);

                    // Create offline Alert
                    createOfflineAlert(status.getDeviceId());

                    // Broadcast offline event
                    socketPublisher.publish("device-offline", status);
                }
            }
        }
    }

    private void createOfflineAlert(String deviceId) {
        String type = "DEVICE_OFFLINE";
        Optional<Alert> existing = alertRepository.findFirstByDeviceIdAndTypeAndResolvedFalseOrderByCreatedAtDesc(deviceId, type);
        if (existing.isEmpty()) {
            Alert alert = new Alert(deviceId, type, "Device connection timed out (no heartbeat for >60 seconds)");
            alertRepository.save(alert);
            socketPublisher.publish("alert-created", alert);
        }
    }
}
