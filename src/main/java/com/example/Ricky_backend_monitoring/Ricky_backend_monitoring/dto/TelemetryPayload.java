package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto;

import java.time.LocalDateTime;

public class TelemetryPayload {

    private String deviceId;
    private String firmwareVersion;
    private String timestamp; // ISO 8601 string, e.g. "2026-06-07T15:10:00Z"
    private GpsInfo gps;
    private BatteryInfo battery;
    private SosInfo sos;
    private HardwareInfo hardware;
    private ImuInfo imu;
    private ServicesInfo services;
    private Object internet;
    private SystemInfo system;

    public TelemetryPayload() {}

    // Getters and Setters
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getFirmwareVersion() { return firmwareVersion; }
    public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public GpsInfo getGps() { return gps; }
    public void setGps(GpsInfo gps) { this.gps = gps; }

    public BatteryInfo getBattery() { return battery; }
    public void setBattery(BatteryInfo battery) { this.battery = battery; }

    public SosInfo getSos() { return sos; }
    public void setSos(SosInfo sos) { this.sos = sos; }

    public HardwareInfo getHardware() { return hardware; }
    public void setHardware(HardwareInfo hardware) { this.hardware = hardware; }

    public ImuInfo getImu() { return imu; }
    public void setImu(ImuInfo imu) { this.imu = imu; }

    public ServicesInfo getServices() { return services; }
    public void setServices(ServicesInfo services) { this.services = services; }

    public Object getInternet() { return internet; }
    public void setInternet(Object internet) { this.internet = internet; }

    public SystemInfo getSystem() { return system; }
    public void setSystem(SystemInfo system) { this.system = system; }

    // Nested Classes
    public static class GpsInfo {
        private Double lat;
        private Double lng;
        private Double speed;
        private Boolean fixed;

        public Double getLat() { return lat; }
        public void setLat(Double lat) { this.lat = lat; }
        public Double getLng() { return lng; }
        public void setLng(Double lng) { this.lng = lng; }
        public Double getSpeed() { return speed; }
        public void setSpeed(Double speed) { this.speed = speed; }
        public Boolean getFixed() { return fixed; }
        public void setFixed(Boolean fixed) { this.fixed = fixed; }
    }

    public static class BatteryInfo {
        private Integer percentage;
        private Double voltage;
        private Boolean charging;
        private String powerSource;

        public Integer getPercentage() { return percentage; }
        public void setPercentage(Integer percentage) { this.percentage = percentage; }
        public Double getVoltage() { return voltage; }
        public void setVoltage(Double voltage) { this.voltage = voltage; }
        public Boolean getCharging() { return charging; }
        public void setCharging(Boolean charging) { this.charging = charging; }
        public String getPowerSource() { return powerSource; }
        public void setPowerSource(String powerSource) { this.powerSource = powerSource; }
    }

    public static class SosInfo {
        private Boolean active;
        private String source;

        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
    }

    public static class HardwareInfo {
        private Boolean espConnected;
        private Boolean gpsConnected;
        private Boolean imuConnected;
        private Boolean displayConnected;

        public Boolean getEspConnected() { return espConnected; }
        public void setEspConnected(Boolean espConnected) { this.espConnected = espConnected; }
        public Boolean getGpsConnected() { return gpsConnected; }
        public void setGpsConnected(Boolean gpsConnected) { this.gpsConnected = gpsConnected; }
        public Boolean getImuConnected() { return imuConnected; }
        public void setImuConnected(Boolean imuConnected) { this.imuConnected = imuConnected; }
        public Boolean getDisplayConnected() { return displayConnected; }
        public void setDisplayConnected(Boolean displayConnected) { this.displayConnected = displayConnected; }
    }

    public static class ImuInfo {
        private Double accelX;
        private Double accelY;
        private Double accelZ;
        private Double gyroX;
        private Double gyroY;
        private Double gyroZ;

        public Double getAccelX() { return accelX; }
        public void setAccelX(Double accelX) { this.accelX = accelX; }
        public Double getAccelY() { return accelY; }
        public void setAccelY(Double accelY) { this.accelY = accelY; }
        public Double getAccelZ() { return accelZ; }
        public void setAccelZ(Double accelZ) { this.accelZ = accelZ; }
        public Double getGyroX() { return gyroX; }
        public void setGyroX(Double gyroX) { this.gyroX = gyroX; }
        public Double getGyroY() { return gyroY; }
        public void setGyroY(Double gyroY) { this.gyroY = gyroY; }
        public Double getGyroZ() { return gyroZ; }
        public void setGyroZ(Double gyroZ) { this.gyroZ = gyroZ; }
    }

    public static class ServicesInfo {
        private Boolean posterBookingRunning;
        private Boolean telemetryServiceRunning;

        public Boolean getPosterBookingRunning() { return posterBookingRunning; }
        public void setPosterBookingRunning(Boolean posterBookingRunning) { this.posterBookingRunning = posterBookingRunning; }
        public Boolean getTelemetryServiceRunning() { return telemetryServiceRunning; }
        public void setTelemetryServiceRunning(Boolean telemetryServiceRunning) { this.telemetryServiceRunning = telemetryServiceRunning; }
    }

    public static class InternetInfo {
        private Boolean connected;
        private Integer signalStrength;

        public Boolean getConnected() { return connected; }
        public void setConnected(Boolean connected) { this.connected = connected; }
        public Integer getSignalStrength() { return signalStrength; }
        public void setSignalStrength(Integer signalStrength) { this.signalStrength = signalStrength; }
    }

    public static class SystemInfo {
        private Integer cpu;
        private Integer ram;
        private Integer disk;
        private Double temp;

        public Integer getCpu() { return cpu; }
        public void setCpu(Integer cpu) { this.cpu = cpu; }
        public Integer getRam() { return ram; }
        public void setRam(Integer ram) { this.ram = ram; }
        public Integer getDisk() { return disk; }
        public void setDisk(Integer disk) { this.disk = disk; }
        public Double getTemp() { return temp; }
        public void setTemp(Double temp) { this.temp = temp; }
    }
}
