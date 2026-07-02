//package com.employee.AttendanceService.client;
//
//import com.employee.AttendanceService.dto.request.UpdateEmployeeStatusPayload;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE", url = "http://localhost:7072")
//public interface EmployeeClient {
//
//    @PostMapping("/api/employee/internal/updateEmployeeStatus")
//    void updateEmployeeStatus(@RequestBody UpdateEmployeeStatusPayload payload);
//}
