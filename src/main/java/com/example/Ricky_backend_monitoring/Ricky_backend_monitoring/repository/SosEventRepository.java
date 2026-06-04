package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.SosEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SosEventRepository extends JpaRepository<SosEvent, Long> {
    List<SosEvent> findAllByDeviceIdOrderByTimestampDesc(String deviceId);
    Optional<SosEvent> findFirstByDeviceIdAndResolvedFalseOrderByTimestampDesc(String deviceId);
}
