package com.employee.AdminService.client;

import com.employee.AdminService.dto.request.EmployeeProfilePayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE", url = "http://localhost:8082")
public interface EmployeeClient {
    @PostMapping("/api/employee/internal/create-profile")
    void createProfile(@RequestBody EmployeeProfilePayload payload);
}
