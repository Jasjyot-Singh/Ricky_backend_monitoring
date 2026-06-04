package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RickyBackendMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(RickyBackendMonitoringApplication.class, args);
	}

}
