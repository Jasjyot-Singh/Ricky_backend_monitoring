package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.service;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.Alert;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository.AlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public List<Alert> getUnresolvedAlerts() {
        return alertRepository.findAllByResolvedFalseOrderByCreatedAtDesc();
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Alert> getAlertsByDevice(String deviceId) {
        return alertRepository.findAllByDeviceIdOrderByCreatedAtDesc(deviceId);
    }

    @Transactional
    public Optional<Alert> resolveAlert(Long alertId) {
        Optional<Alert> alertOpt = alertRepository.findById(alertId);
        if (alertOpt.isPresent()) {
            Alert alert = alertOpt.get();
            alert.setResolved(true);
            alert.setResolvedAt(LocalDateTime.now());
            alertRepository.save(alert);
            return Optional.of(alert);
        }
        return Optional.empty();
    }
}
