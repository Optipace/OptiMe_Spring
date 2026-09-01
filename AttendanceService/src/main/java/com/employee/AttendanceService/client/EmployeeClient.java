package com.employee.AttendanceService.client;

import com.employee.AttendanceService.dto.request.UpdateEmployeeStatusPayload;
import com.employee.AttendanceService.dto.response.ApiResponse;
import com.employee.AttendanceService.dto.response.ListOfEmployeeIdResponse;
import com.employee.AttendanceService.dto.response.SingleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE", url = "http://localhost:7072")
@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE")
public interface EmployeeClient {

    @PostMapping("/api/employee/internal/updateEmployeeStatus")
    void updateEmployeeStatus(@RequestBody UpdateEmployeeStatusPayload payload);

    @GetMapping("/api/employee/internal/checkWorkTypeById")
    boolean checkWorkTypeIdExists(@RequestParam("workTypeId")Long workTypeId);

    @GetMapping("/api/employee/internal/getAllEmployeeId")
    SingleResponse<ListOfEmployeeIdResponse> getAllEmployeeId();

    @GetMapping("/api/employee/internal/getEmployeeName")
    SingleResponse<?> getEmployeeName(@RequestParam("employeeId") Long employeeId);

    @GetMapping("/api/employee/internal/getEmployeeOfficeId")
    public SingleResponse<Long> getEmployeeOfficeId(@RequestParam Long employeeId);

}
