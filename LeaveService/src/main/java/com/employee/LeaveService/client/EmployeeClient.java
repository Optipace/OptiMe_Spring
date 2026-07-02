package com.employee.LeaveService.client;

import com.employee.LeaveService.dto.request.UpdateEmployeeStatusPayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE", url = "http://localhost:7072")
public interface EmployeeClient {

    @GetMapping("/api/employee/internal/checkEmployeeByEmployeeId")
    public boolean checkEmployeeByEmployeeId(@RequestParam("employeeId") String employeeId);

    @PostMapping("/api/employee/internal/updateEmployeeStatus")
    void updateEmployeeStatus(@RequestBody UpdateEmployeeStatusPayload payload);
}
