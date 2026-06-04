package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.LocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LocationHistoryRepository extends JpaRepository<LocationHistory, Long> {
    
    @Query("SELECT lh FROM LocationHistory lh WHERE lh.deviceId = :deviceId " +
           "AND lh.timestamp BETWEEN :fromDate AND :toDate ORDER BY lh.timestamp ASC")
    List<LocationHistory> findRouteHistory(
            @Param("deviceId") String deviceId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
