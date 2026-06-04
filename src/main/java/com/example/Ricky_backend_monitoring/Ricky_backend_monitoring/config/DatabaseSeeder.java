package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.config;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.*;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// @Component
public class DatabaseSeeder implements CommandLineRunner {

    private final DeviceRepository deviceRepository;
    private final LiveStatusRepository liveStatusRepository;
    private final AlertRepository alertRepository;
    private final LocationHistoryRepository locationHistoryRepository;
    private final SosEventRepository sosEventRepository;

    public DatabaseSeeder(DeviceRepository deviceRepository,
                          LiveStatusRepository liveStatusRepository,
                          AlertRepository alertRepository,
                          LocationHistoryRepository locationHistoryRepository,
                          SosEventRepository sosEventRepository) {
        this.deviceRepository = deviceRepository;
        this.liveStatusRepository = liveStatusRepository;
        this.alertRepository = alertRepository;
        this.locationHistoryRepository = locationHistoryRepository;
        this.sosEventRepository = sosEventRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (deviceRepository.count() > 0) {
            System.out.println("-> Database already seeded with devices. Skipping database seeding.");
            return;
        }

        System.out.println("-> Starting Neon DB Database Seeding for Ricky Fleet HQ Pilot...");
        LocalDateTime now = LocalDateTime.now();

        // 1. Create and save Devices
        Device d1 = new Device("RICKY-001", "MH12AB1001", "Ramesh Kumar", "1.0.0");
        Device d2 = new Device("RICKY-002", "MH12AB1002", "Suresh Patil", "1.0.0");
        Device d3 = new Device("RICKY-003", "MH12AB1003", "Aniket Shinde", "1.0.0");
        Device d4 = new Device("RICKY-004", "MH12AB1004", "Vijay Tambe", "1.0.0");
        Device d5 = new Device("RICKY-005", "MH12AB1005", "Ganesh Gawde", "1.0.0");
        Device d6 = new Device("RICKY-006", "MH12AB1006", "Dinesh More", "1.0.0");
        deviceRepository.saveAll(List.of(d1, d2, d3, d4, d5, d6));

        // 2. Create LiveStatus records matching the screen mockup
        // RICKY-001 (Online, Fixed, battery 82%, charging, safe)
        LiveStatus s1 = new LiveStatus();
        s1.setDeviceId("RICKY-001");
        s1.setOnline(true);
        s1.setLastSeen(now.minusSeconds(8));
        s1.setLatitude(18.6201);
        s1.setLongitude(73.8567);
        s1.setSpeed(28.5);
        s1.setGpsFix(true);
        s1.setBatteryPercentage(82);
        s1.setBatteryVoltage(12.4);
        s1.setCharging(true);
        s1.setPowerSource("charging");
        s1.setInternetConnected(true);
        s1.setInternetSignalStrength(-64);
        s1.setSosActive(false);
        s1.setSosSource(null);
        s1.setGpsConnected(true);
        s1.setEspConnected(true);
        s1.setImuConnected(true);
        s1.setDisplayConnected(true);
        s1.setCpuUsage(24);
        s1.setRamUsage(48);
        s1.setDiskUsage(38);
        s1.setCpuTemperature(45.2);
        s1.setUpdatedAt(now);
        s1.setFirmwareVersion("1.0.0");

        // RICKY-002 (Online, Fixed, battery 67%, charging, safe)
        LiveStatus s2 = new LiveStatus();
        s2.setDeviceId("RICKY-002");
        s2.setOnline(true);
        s2.setLastSeen(now.minusSeconds(12));
        s2.setLatitude(18.5520);
        s2.setLongitude(73.7804);
        s2.setSpeed(12.0);
        s2.setGpsFix(true);
        s2.setBatteryPercentage(67);
        s2.setBatteryVoltage(12.2);
        s2.setCharging(true);
        s2.setPowerSource("charging");
        s2.setInternetConnected(true);
        s2.setInternetSignalStrength(-68);
        s2.setSosActive(false);
        s2.setSosSource(null);
        s2.setGpsConnected(true);
        s2.setEspConnected(true);
        s2.setImuConnected(true);
        s2.setDisplayConnected(true);
        s2.setCpuUsage(30);
        s2.setRamUsage(51);
        s2.setDiskUsage(38);
        s2.setCpuTemperature(46.8);
        s2.setUpdatedAt(now);
        s2.setFirmwareVersion("1.0.0");

        // RICKY-003 (Online, Searching, battery 18%, battery power, low battery alert)
        LiveStatus s3 = new LiveStatus();
        s3.setDeviceId("RICKY-003");
        s3.setOnline(true);
        s3.setLastSeen(now.minusSeconds(15));
        s3.setLatitude(18.6452);
        s3.setLongitude(73.8951);
        s3.setSpeed(0.0);
        s3.setGpsFix(false); // GPS Status: Searching
        s3.setBatteryPercentage(18);
        s3.setBatteryVoltage(11.6);
        s3.setCharging(false);
        s3.setPowerSource("battery");
        s3.setInternetConnected(true);
        s3.setInternetSignalStrength(-72);
        s3.setSosActive(false);
        s3.setSosSource(null);
        s3.setGpsConnected(true);
        s3.setEspConnected(true);
        s3.setImuConnected(true);
        s3.setDisplayConnected(true);
        s3.setCpuUsage(35);
        s3.setRamUsage(54);
        s3.setDiskUsage(39);
        s3.setCpuTemperature(49.1);
        s3.setUpdatedAt(now);
        s3.setFirmwareVersion("1.0.0");

        // RICKY-004 (Online, Fixed, battery 73%, charging, safe)
        LiveStatus s4 = new LiveStatus();
        s4.setDeviceId("RICKY-004");
        s4.setOnline(true);
        s4.setLastSeen(now.minusSeconds(9));
        s4.setLatitude(18.4987);
        s4.setLongitude(73.8275);
        s4.setSpeed(32.4);
        s4.setGpsFix(true);
        s4.setBatteryPercentage(73);
        s4.setBatteryVoltage(12.1);
        s4.setCharging(true);
        s4.setPowerSource("charging");
        s4.setInternetConnected(true);
        s4.setInternetSignalStrength(-62);
        s4.setSosActive(false);
        s4.setSosSource(null);
        s4.setGpsConnected(true);
        s4.setEspConnected(true);
        s4.setImuConnected(true);
        s4.setDisplayConnected(true);
        s4.setCpuUsage(22);
        s4.setRamUsage(47);
        s4.setDiskUsage(38);
        s4.setCpuTemperature(44.0);
        s4.setUpdatedAt(now);
        s4.setFirmwareVersion("1.0.0");

        // RICKY-005 (Online, Fixed, battery 74%, charging, SOS ACTIVE from Internal Button)
        LiveStatus s5 = new LiveStatus();
        s5.setDeviceId("RICKY-005");
        s5.setOnline(true);
        s5.setLastSeen(now.minusSeconds(5));
        s5.setLatitude(18.5033);
        s5.setLongitude(73.9532);
        s5.setSpeed(15.2);
        s5.setGpsFix(true);
        s5.setBatteryPercentage(74);
        s5.setBatteryVoltage(12.3);
        s5.setCharging(true);
        s5.setPowerSource("charging");
        s5.setInternetConnected(true);
        s5.setInternetSignalStrength(-60);
        s5.setSosActive(true);
        s5.setSosSource("Internal Button");
        s5.setGpsConnected(true);
        s5.setEspConnected(true);
        s5.setImuConnected(true);
        s5.setDisplayConnected(true);
        s5.setCpuUsage(28);
        s5.setRamUsage(50);
        s5.setDiskUsage(38);
        s5.setCpuTemperature(46.0);
        s5.setUpdatedAt(now);
        s5.setFirmwareVersion("1.0.0");

        // RICKY-006 (Offline, Fixed, battery 45%, not charging, safe, last seen 25 min ago)
        LiveStatus s6 = new LiveStatus();
        s6.setDeviceId("RICKY-006");
        s6.setOnline(false);
        s6.setLastSeen(now.minusMinutes(25));
        s6.setLatitude(18.5204);
        s6.setLongitude(73.8567);
        s6.setSpeed(0.0);
        s6.setGpsFix(true);
        s6.setBatteryPercentage(45);
        s6.setBatteryVoltage(11.9);
        s6.setCharging(false);
        s6.setPowerSource("battery");
        s6.setInternetConnected(false);
        s6.setInternetSignalStrength(0);
        s6.setSosActive(false);
        s6.setSosSource(null);
        s6.setGpsConnected(false);
        s6.setEspConnected(true);
        s6.setImuConnected(true);
        s6.setDisplayConnected(true);
        s6.setCpuUsage(0);
        s6.setRamUsage(0);
        s6.setDiskUsage(38);
        s6.setCpuTemperature(0.0);
        s6.setUpdatedAt(now.minusMinutes(25));
        s6.setFirmwareVersion("1.0.0");

        liveStatusRepository.saveAll(List.of(s1, s2, s3, s4, s5, s6));

        // 3. Save initial LocationHistory for breadcrumbs
        locationHistoryRepository.save(new LocationHistory("RICKY-001", 18.6200, 73.8560, 20.0, now.minusMinutes(5)));
        locationHistoryRepository.save(new LocationHistory("RICKY-001", 18.6201, 73.8567, 28.5, now));

        locationHistoryRepository.save(new LocationHistory("RICKY-002", 18.5510, 73.7800, 10.0, now.minusMinutes(5)));
        locationHistoryRepository.save(new LocationHistory("RICKY-002", 18.5520, 73.7804, 12.0, now));

        locationHistoryRepository.save(new LocationHistory("RICKY-003", 18.6452, 73.8951, 0.0, now));

        locationHistoryRepository.save(new LocationHistory("RICKY-004", 18.4980, 73.8270, 25.0, now.minusMinutes(5)));
        locationHistoryRepository.save(new LocationHistory("RICKY-004", 18.4987, 73.8275, 32.4, now));

        locationHistoryRepository.save(new LocationHistory("RICKY-005", 18.5020, 73.9520, 10.0, now.minusMinutes(5)));
        locationHistoryRepository.save(new LocationHistory("RICKY-005", 18.5033, 73.9532, 15.2, now));

        // 4. Create active diagnostic alerts matching Card 1, 2, and 3
        // Card 1: SOS ACTIVE, RICKY-005, Hadapsar, Pune
        SosEvent sosEvent = new SosEvent("RICKY-005", "Internal Button", now.minusMinutes(2));
        sosEventRepository.save(sosEvent);

        Alert alert1 = new Alert("RICKY-005", "SOS", "Active SOS triggered from: Internal Button (Hadapsar, Pune)");
        alert1.setCreatedAt(now.minusMinutes(2));
        
        // Card 2: LOW BATTERY, RICKY-003, Battery at 18%
        Alert alert2 = new Alert("RICKY-003", "LOW_BATTERY", "Device battery percentage is critical: 18% (Voltage: 11.6V)");
        alert2.setCreatedAt(now.minusMinutes(5));

        // Card 3: DEVICE OFFLINE, RICKY-006, Last seen 25 min ago
        Alert alert3 = new Alert("RICKY-006", "DEVICE_OFFLINE", "Device went offline. Last seen 25 min ago.");
        alert3.setCreatedAt(now.minusMinutes(25));

        alertRepository.saveAll(List.of(alert1, alert2, alert3));

        System.out.println("-> Neon DB database successfully seeded with 6 pilot devices and Mockup alerts.");
    }
}
