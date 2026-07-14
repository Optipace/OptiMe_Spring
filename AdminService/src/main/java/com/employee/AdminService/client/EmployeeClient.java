package com.employee.AdminService.client;

import com.employee.AdminService.dto.request.EmployeeProfilePayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE", url = "http://localhost:7072")
@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE")
public interface EmployeeClient {
    @PostMapping("/api/employee/internal/createProfile")
    void createProfile(@RequestBody EmployeeProfilePayload payload);

    @DeleteMapping("/api/employee/internal/deleteIdentity")
    void deleteIdentity(@RequestParam("employeeId") String employeeId);
}
