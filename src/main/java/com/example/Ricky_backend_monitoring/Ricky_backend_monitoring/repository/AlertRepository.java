package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByResolvedFalseOrderByCreatedAtDesc();
    List<Alert> findAllByOrderByCreatedAtDesc();
    List<Alert> findAllByDeviceIdOrderByCreatedAtDesc(String deviceId);
    
    Optional<Alert> findFirstByDeviceIdAndTypeAndResolvedFalseOrderByCreatedAtDesc(String deviceId, String type);

    @Query("SELECT a FROM Alert a WHERE a.resolved = true AND a.resolvedAt < :cutoff")
    List<Alert> findResolvedAlertsOlderThan(@Param("cutoff") LocalDateTime cutoff);
}
