package com.employee.AuthService.client;

import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.dto.response.LeaveTypeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "LEAVE-SERVICE")
public interface LeaveClient {
    @GetMapping("/api/leave/internal/getLeaveTypeList")
    public ApiResponse<List<LeaveTypeResponse>> getLeaveTypeList();
}
