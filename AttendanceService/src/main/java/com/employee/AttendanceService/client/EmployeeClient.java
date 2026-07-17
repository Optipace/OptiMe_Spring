package com.employee.AttendanceService.client;

import com.employee.AttendanceService.dto.request.UpdateEmployeeStatusPayload;
import com.employee.AttendanceService.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE", url = "http://localhost:7072")
@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE")
public interface EmployeeClient {

    @PostMapping("/api/employee/internal/updateEmployeeStatus")
    void updateEmployeeStatus(@RequestBody UpdateEmployeeStatusPayload payload);

    @GetMapping("/api/employee/internal/getWorkTypeById")
    ApiResponse<Long> getWorkTypeById(@RequestParam("workTypeId")Long workTypeId);
}
