package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "live_status")
public class LiveStatus {

    @Id
    @Column(name = "device_id", nullable = false, length = 50)
    private String deviceId;

    @Column(name = "firmware_version", length = 20)
    private String firmwareVersion;

    @Column(name = "online", nullable = false)
    private Boolean online = false;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "gps_fix")
    private Boolean gpsFix = false;

    @Column(name = "gps_connected")
    private Boolean gpsConnected = false;

    @Column(name = "battery_percentage")
    private Integer batteryPercentage;

    @Column(name = "battery_voltage")
    private Double batteryVoltage;

    @Column(name = "charging")
    private Boolean charging = false;

    @Column(name = "power_source", length = 20)
    private String powerSource;

    @Column(name = "internet_connected")
    private Boolean internetConnected = false;

    @Column(name = "internet_signal_strength")
    private Integer internetSignalStrength;

    @Column(name = "imu_connected")
    private Boolean imuConnected = false;

    @Column(name = "imu_accel_x")
    private Double imuAccelX;

    @Column(name = "imu_accel_y")
    private Double imuAccelY;

    @Column(name = "imu_accel_z")
    private Double imuAccelZ;

    @Column(name = "imu_gyro_x")
    private Double imuGyroX;

    @Column(name = "imu_gyro_y")
    private Double imuGyroY;

    @Column(name = "imu_gyro_z")
    private Double imuGyroZ;

    @Column(name = "cpu_usage")
    private Integer cpuUsage;

    @Column(name = "ram_usage")
    private Integer ramUsage;

    @Column(name = "disk_usage")
    private Integer diskUsage;

    @Column(name = "cpu_temperature")
    private Double cpuTemperature;

    @Column(name = "esp_connected")
    private Boolean espConnected = false;

    @Column(name = "display_connected")
    private Boolean displayConnected = false;

    @Column(name = "poster_booking_running")
    private Boolean posterBookingRunning = false;

    @Column(name = "telemetry_service_running")
    private Boolean telemetryServiceRunning = false;

    @Column(name = "sos_active")
    private Boolean sosActive = false;

    @Column(name = "sos_source", length = 30)
    private String sosSource;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public LiveStatus() {}

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Boolean getGpsFix() {
        return gpsFix;
    }

    public void setGpsFix(Boolean gpsFix) {
        this.gpsFix = gpsFix;
    }

    public Boolean getGpsConnected() {
        return gpsConnected;
    }

    public void setGpsConnected(Boolean gpsConnected) {
        this.gpsConnected = gpsConnected;
    }

    public Integer getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(Integer batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public Double getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(Double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public Boolean getCharging() {
        return charging;
    }

    public void setCharging(Boolean charging) {
        this.charging = charging;
    }

    public String getPowerSource() {
        return powerSource;
    }

    public void setPowerSource(String powerSource) {
        this.powerSource = powerSource;
    }

    public Boolean getInternetConnected() {
        return internetConnected;
    }

    public void setInternetConnected(Boolean internetConnected) {
        this.internetConnected = internetConnected;
    }

    public Integer getInternetSignalStrength() {
        return internetSignalStrength;
    }

    public void setInternetSignalStrength(Integer internetSignalStrength) {
        this.internetSignalStrength = internetSignalStrength;
    }

    public Boolean getImuConnected() {
        return imuConnected;
    }

    public void setImuConnected(Boolean imuConnected) {
        this.imuConnected = imuConnected;
    }

    public Double getImuAccelX() {
        return imuAccelX;
    }

    public void setImuAccelX(Double imuAccelX) {
        this.imuAccelX = imuAccelX;
    }

    public Double getImuAccelY() {
        return imuAccelY;
    }

    public void setImuAccelY(Double imuAccelY) {
        this.imuAccelY = imuAccelY;
    }

    public Double getImuAccelZ() {
        return imuAccelZ;
    }

    public void setImuAccelZ(Double imuAccelZ) {
        this.imuAccelZ = imuAccelZ;
    }

    public Double getImuGyroX() {
        return imuGyroX;
    }

    public void setImuGyroX(Double imuGyroX) {
        this.imuGyroX = imuGyroX;
    }

    public Double getImuGyroY() {
        return imuGyroY;
    }

    public void setImuGyroY(Double imuGyroY) {
        this.imuGyroY = imuGyroY;
    }

    public Double getImuGyroZ() {
        return imuGyroZ;
    }

    public void setImuGyroZ(Double imuGyroZ) {
        this.imuGyroZ = imuGyroZ;
    }

    public Integer getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Integer cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Integer getRamUsage() {
        return ramUsage;
    }

    public void setRamUsage(Integer ramUsage) {
        this.ramUsage = ramUsage;
    }

    public Integer getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(Integer diskUsage) {
        this.diskUsage = diskUsage;
    }

    public Double getCpuTemperature() {
        return cpuTemperature;
    }

    public void setCpuTemperature(Double cpuTemperature) {
        this.cpuTemperature = cpuTemperature;
    }

    public Boolean getEspConnected() {
        return espConnected;
    }

    public void setEspConnected(Boolean espConnected) {
        this.espConnected = espConnected;
    }

    public Boolean getDisplayConnected() {
        return displayConnected;
    }

    public void setDisplayConnected(Boolean displayConnected) {
        this.displayConnected = displayConnected;
    }

    public Boolean getPosterBookingRunning() {
        return posterBookingRunning;
    }

    public void setPosterBookingRunning(Boolean posterBookingRunning) {
        this.posterBookingRunning = posterBookingRunning;
    }

    public Boolean getTelemetryServiceRunning() {
        return telemetryServiceRunning;
    }

    public void setTelemetryServiceRunning(Boolean telemetryServiceRunning) {
        this.telemetryServiceRunning = telemetryServiceRunning;
    }

    public Boolean getSosActive() {
        return sosActive;
    }

    public void setSosActive(Boolean sosActive) {
        this.sosActive = sosActive;
    }

    public String getSosSource() {
        return sosSource;
    }

    public void setSosSource(String sosSource) {
        this.sosSource = sosSource;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
