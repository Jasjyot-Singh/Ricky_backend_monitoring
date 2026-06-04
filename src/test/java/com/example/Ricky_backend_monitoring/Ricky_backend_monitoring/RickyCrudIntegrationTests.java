package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.controller.*;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto.*;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RickyCrudIntegrationTests {

    @Autowired
    private AuthController authController;

    @Autowired
    private TelemetryController telemetryController;

    @Autowired
    private FleetController fleetController;

    @Autowired
    private DeviceController deviceController;

    @Autowired
    private AlertController alertController;

    @Test
    public void testAllCrudOperationsProgrammatically() {
        System.out.println("==================================================================");
        System.out.println("          STARTING PROGRAMMATIC CRUD INTEGRATION TEST RUN         ");
        System.out.println("==================================================================");

        // 1. Operator Login
        System.out.println("\n[TEST 1] Operator Login...");
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername("operator_admin");
        loginReq.setPassword("securepassword123");
        ResponseEntity<LoginResponse> loginResp = authController.login(loginReq);
        assertEquals(200, loginResp.getStatusCode().value());
        System.out.println("Login Successful! Access Token: " + loginResp.getBody().getAccessToken());

        // 2. Submit Telemetry (Create/Update LiveStatus & LocationHistory)
        System.out.println("\n[TEST 2] Submit Telemetry (POST /api/v1/telemetry equivalent)...");
        TelemetryPayload telemetry = new TelemetryPayload();
        telemetry.setDeviceId("RICKY-001");
        telemetry.setFirmwareVersion("1.0.0");
        
        TelemetryPayload.GpsInfo gps = new TelemetryPayload.GpsInfo();
        gps.setLat(19.87654);
        gps.setLng(75.34321);
        gps.setSpeed(35.0);
        gps.setFixed(true);
        telemetry.setGps(gps);

        TelemetryPayload.BatteryInfo battery = new TelemetryPayload.BatteryInfo();
        battery.setPercentage(15); // Trigger low battery alert (< 20%)
        battery.setVoltage(11.2);
        battery.setCharging(false);
        battery.setPowerSource("battery");
        telemetry.setBattery(battery);

        TelemetryPayload.SosInfo sos = new TelemetryPayload.SosInfo();
        sos.setActive(true);
        sos.setSource("external_button_1");
        telemetry.setSos(sos);

        TelemetryPayload.HardwareInfo hw = new TelemetryPayload.HardwareInfo();
        hw.setEspConnected(true);
        hw.setGpsConnected(true);
        hw.setImuConnected(true);
        hw.setDisplayConnected(true);
        telemetry.setHardware(hw);

        telemetry.setInternet(true);

        ResponseEntity<Map<String, Object>> telResp = telemetryController.receiveTelemetry(telemetry);
        assertEquals(200, telResp.getStatusCode().value());
        System.out.println("Telemetry Ingestion Response: " + telResp.getBody());

        // 3. Submit Heartbeat
        System.out.println("\n[TEST 3] Submit Heartbeat...");
        HeartbeatPayload heartbeat = new HeartbeatPayload();
        heartbeat.setDeviceId("RICKY-001");
        ResponseEntity<Map<String, Object>> hbResp = telemetryController.receiveHeartbeat(heartbeat);
        assertEquals(200, hbResp.getStatusCode().value());
        System.out.println("Heartbeat Response: " + hbResp.getBody());

        // 4. Get Fleet Status
        System.out.println("\n[TEST 4] Get Fleet Live Status...");
        ResponseEntity<List<Map<String, Object>>> fleetResp = fleetController.getStatus(null);
        assertEquals(200, fleetResp.getStatusCode().value());
        System.out.println("Fleet Status List Size: " + fleetResp.getBody().size());
        System.out.println("First Device Record: " + fleetResp.getBody().get(0));

        // 5. Get Fleet aggregate statistics
        System.out.println("\n[TEST 5] Get Fleet Aggregate Stats...");
        ResponseEntity<FleetStats> statsResp = fleetController.getStats();
        assertEquals(200, statsResp.getStatusCode().value());
        System.out.println("Total Devices: " + statsResp.getBody().getTotalDevices());
        System.out.println("Online Devices: " + statsResp.getBody().getOnlineDevices());
        System.out.println("Active SOS Alerts: " + statsResp.getBody().getActiveSOS());

        // 6. Get Active alerts for fleet
        System.out.println("\n[TEST 6] Get Active Alerts...");
        ResponseEntity<List<Alert>> activeAlertsResp = fleetController.getActiveAlerts();
        assertEquals(200, activeAlertsResp.getStatusCode().value());
        System.out.println("Active Alerts Count: " + activeAlertsResp.getBody().size());
        for (Alert a : activeAlertsResp.getBody()) {
            System.out.println(" - Alert: ID=" + a.getId() + ", Type=" + a.getType() + ", Msg=" + a.getMessage());
        }

        // 7. Get Device Details
        System.out.println("\n[TEST 7] Get Single Device Status Details...");
        ResponseEntity<Map<String, Object>> devDetailsResp = deviceController.getDevice("RICKY-001");
        assertEquals(200, devDetailsResp.getStatusCode().value());
        System.out.println("Device Status Details: " + devDetailsResp.getBody());

        // 8. Get Device Route History
        System.out.println("\n[TEST 8] Get Route History coordinates...");
        ResponseEntity<List<Map<String, Object>>> routeResp = deviceController.getRoute("RICKY-001", null, null, null, null);
        assertEquals(200, routeResp.getStatusCode().value());
        System.out.println("Route Coordinate Count: " + routeResp.getBody().size());

        // 9. Queue Remote Command
        System.out.println("\n[TEST 9] Queue Remote Command...");
        CommandRequest cmdReq = new CommandRequest();
        cmdReq.setCommand("REBOOT_DEVICE");
        ResponseEntity<Map<String, Object>> cmdResp = deviceController.sendCommand("RICKY-001", cmdReq);
        assertEquals(201, cmdResp.getStatusCode().value());
        System.out.println("Command Response: " + cmdResp.getBody());
        Long commandId = ((Number) cmdResp.getBody().get("commandId")).longValue();

        // 10. Get Command History
        System.out.println("\n[TEST 10] Get Command History...");
        ResponseEntity<List<Map<String, Object>>> historyResp = deviceController.getCommandHistory("RICKY-001");
        assertEquals(200, historyResp.getStatusCode().value());
        System.out.println("Command History Size: " + historyResp.getBody().size());

        // 11. Poll pending commands
        System.out.println("\n[TEST 11] RPi Polls Pending Commands...");
        ResponseEntity<List<DeviceCommand>> pendingResp = deviceController.getPendingCommands("RICKY-001");
        assertEquals(200, pendingResp.getStatusCode().value());
        System.out.println("Pending Commands Count: " + pendingResp.getBody().size());

        // 12. Confirm command execution
        System.out.println("\n[TEST 12] RPi Responds to Command Execution...");
        Map<String, String> responsePayload = Map.of("status", "executed", "response", "Reboot sequence initiated");
        ResponseEntity<Map<String, Object>> execResp = deviceController.respondToCommand("RICKY-001", commandId, responsePayload);
        assertEquals(200, execResp.getStatusCode().value());
        System.out.println("Execution response: " + execResp.getBody());

        // 13. Get all alerts history
        System.out.println("\n[TEST 13] Get All Alerts History...");
        ResponseEntity<List<Alert>> allAlertsResp = alertController.getAllAlerts();
        assertEquals(200, allAlertsResp.getStatusCode().value());
        System.out.println("Total Alerts in DB: " + allAlertsResp.getBody().size());

        // 14. Resolve active alerts
        System.out.println("\n[TEST 14] Resolve Active Alert...");
        if (!activeAlertsResp.getBody().isEmpty()) {
            Long alertId = activeAlertsResp.getBody().get(0).getId();
            ResponseEntity<Map<String, Object>> resolveResp = alertController.resolveAlert(alertId);
            assertEquals(200, resolveResp.getStatusCode().value());
            System.out.println("Alert Resolution response: " + resolveResp.getBody());
        } else {
            System.out.println("No active alerts to resolve.");
        }

        // 15. Operator Logout
        System.out.println("\n[TEST 15] Operator Logout...");
        ResponseEntity<Map<String, Object>> logoutResp = authController.logout(Map.of("refreshToken", "mock-token"));
        assertEquals(200, logoutResp.getStatusCode().value());
        System.out.println("Logout Response: " + logoutResp.getBody());

        System.out.println("\n==================================================================");
        System.out.println("       PROGRAMMATIC CRUD INTEGRATION TEST RUN SUCCESSFUL         ");
        System.out.println("==================================================================");
    }
}
