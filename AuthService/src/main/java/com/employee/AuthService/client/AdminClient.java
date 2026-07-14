package com.employee.AuthService.client;

import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.dto.response.OfficeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "ADMIN-SERVICE")
public interface AdminClient {

    @GetMapping("/api/admin/internal/getOfficeList")
    ApiResponse<List<OfficeResponse>> getOfficeList();

}
