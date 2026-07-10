package com.employee.AuthService.client;

import com.employee.AuthService.dto.request.EmployeeProfilePayload;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.dto.response.EmployeeResponse;
import com.employee.AuthService.dto.response.ListOfOfficeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE", url = "http://localhost:7072")
@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE")
public interface EmployeeClient {
    @GetMapping("/api/employee/internal/get-profile")
    ApiResponse<EmployeeResponse> getProfile(@RequestParam("employeeId") String employeeId);

    @PostMapping("/api/employee/internal/complete-profile")
    void completeProfile(@RequestBody EmployeeProfilePayload profilePayload);

    @GetMapping("/api/employee/internal/getMasterDetails")
    ApiResponse<ListOfOfficeResponse> getMasterDetails();

}