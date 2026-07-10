package com.employee.Gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EmployeeGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeGatewayApplication.class, args);
	}

}
