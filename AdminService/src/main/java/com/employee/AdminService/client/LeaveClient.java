package com.employee.AdminService.client;

import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.LeaveResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

//@FeignClient(name = "LEAVE-SERVICE", url = "http://localhost:7076")
@FeignClient(name = "LEAVE-SERVICE")
public interface LeaveClient {
    @GetMapping("/api/leave/internal/getAllAppliedLeaves")
    ApiResponse<List<LeaveResponse>> getAllAppliedLeaves();
}
