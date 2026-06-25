package com.employee.EmployeeProfileService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EmployeeProfileServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeProfileServiceApplication.class, args);
	}

}
