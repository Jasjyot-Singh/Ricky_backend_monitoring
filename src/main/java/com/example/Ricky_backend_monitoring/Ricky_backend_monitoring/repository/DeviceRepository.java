package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByDeviceId(String deviceId);

    @Query("SELECT d FROM Device d WHERE LOWER(d.deviceId) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(d.vehicleNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(d.driverName) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Device> searchDevices(@Param("search") String search);
}
