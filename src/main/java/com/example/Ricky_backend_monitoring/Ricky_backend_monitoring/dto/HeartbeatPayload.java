package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto;

public class HeartbeatPayload {
    private String deviceId;
    private String timestamp;

    public HeartbeatPayload() {}

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
