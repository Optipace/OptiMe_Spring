package com.employee.LeaveService.client;

import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.EmployeeResponse;
import com.employee.LeaveService.dto.response.SingleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE", url = "http://localhost:7072")
@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE")
public interface EmployeeClient {

//    @GetMapping("/api/employee/internal/checkEmployeeByEmployeeId")
//    ApiResponse<?> checkEmployeeByEmployeeId(@RequestParam("employeeId") String employeeId);


    @GetMapping("/api/employee/internal/getEmployeeById")
    SingleResponse<EmployeeResponse> getEmployeeByEmployeeId(@RequestParam("employeeId") Long employeeId);

    @GetMapping("/api/employee/internal/getEmployeeName")
    SingleResponse<?> getEmployeeName(@RequestParam("employeeId") Long employeeId);
}
