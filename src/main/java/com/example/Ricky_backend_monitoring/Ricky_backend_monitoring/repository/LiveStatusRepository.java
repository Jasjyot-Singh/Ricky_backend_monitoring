package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.LiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveStatusRepository extends JpaRepository<LiveStatus, String> {

    @Query("SELECT ls FROM LiveStatus ls WHERE LOWER(ls.deviceId) IN :deviceIds")
    List<LiveStatus> findAllByDeviceIdIn(@Param("deviceIds") List<String> deviceIds);

    long countByOnlineTrue();
    long countByOnlineFalse();
    long countBySosActiveTrue();
    
    @Query("SELECT count(ls) FROM LiveStatus ls WHERE ls.batteryPercentage < 20")
    long countLowBatteryDevices();
}
