package com.employee.EmployeeProfileService.client;

import com.employee.EmployeeProfileService.dto.response.ApiResponse;
import com.employee.EmployeeProfileService.dto.response.OfficeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ADMIN-SERVICE")
public interface AdminClient {

    @GetMapping("/api/admin/internal/getOfficeByOfficeId")
    ApiResponse<OfficeResponse> getOfficeDetails(@RequestParam("officeId") String officeId);

    @GetMapping("/api/admin/internal/getOfficeNames")
    ApiResponse<List<String>> getOfficeNames();
}
