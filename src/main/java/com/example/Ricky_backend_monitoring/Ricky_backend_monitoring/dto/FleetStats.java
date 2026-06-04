package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto;

public class FleetStats {
    private long totalDevices;
    private long onlineDevices;
    private long offlineDevices;
    private long activeSOS;
    private long lowBatteryDevices;

    public FleetStats() {}

    public FleetStats(long totalDevices, long onlineDevices, long offlineDevices, long activeSOS, long lowBatteryDevices) {
        this.totalDevices = totalDevices;
        this.onlineDevices = onlineDevices;
        this.offlineDevices = offlineDevices;
        this.activeSOS = activeSOS;
        this.lowBatteryDevices = lowBatteryDevices;
    }

    public long getTotalDevices() { return totalDevices; }
    public void setTotalDevices(long totalDevices) { this.totalDevices = totalDevices; }

    public long getOnlineDevices() { return onlineDevices; }
    public void setOnlineDevices(long onlineDevices) { this.onlineDevices = onlineDevices; }

    public long getOfflineDevices() { return offlineDevices; }
    public void setOfflineDevices(long offlineDevices) { this.offlineDevices = offlineDevices; }

    public long getActiveSOS() { return activeSOS; }
    public void setActiveSOS(long activeSOS) { this.activeSOS = activeSOS; }

    public long getLowBatteryDevices() { return lowBatteryDevices; }
    public void setLowBatteryDevices(long lowBatteryDevices) { this.lowBatteryDevices = lowBatteryDevices; }
}
