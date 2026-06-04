package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.DeviceCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceCommandRepository extends JpaRepository<DeviceCommand, Long> {
    List<DeviceCommand> findAllByDeviceIdOrderByCreatedAtDesc(String deviceId);
    List<DeviceCommand> findAllByDeviceIdAndStatusOrderByCreatedAtDesc(String deviceId, String status);
}
