package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.TelemetryLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelemetryLogRepository extends JpaRepository<TelemetryLog, Long> {
    List<TelemetryLog> findAllByDeviceIdOrderByReceivedAtDesc(String deviceId);

    @Query("SELECT tl FROM TelemetryLog tl WHERE tl.deviceId = :deviceId ORDER BY tl.receivedAt DESC")
    List<TelemetryLog> findLatestLogs(@Param("deviceId") String deviceId, Pageable pageable);
}
