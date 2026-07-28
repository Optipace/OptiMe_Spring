package com.employee.AdminService.client;

import com.employee.AdminService.dto.request.UpdateLeaveRequest;
import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.LeaveResponse;
import com.employee.AdminService.dto.response.LeaveTypeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//@FeignClient(name = "LEAVE-SERVICE", url = "http://localhost:7076")
@FeignClient(name = "LEAVE-SERVICE")
public interface LeaveClient {
    @GetMapping("/api/leave/internal/getAllAppliedLeaves")
    ApiResponse<List<LeaveResponse>> getAllAppliedLeaves();

    @GetMapping("/api/leave/internal/getLeaveTypeList")
    public ApiResponse<List<LeaveTypeResponse>> getLeaveTypeList();

    @PutMapping("/api/leave/internal/leaveApproval")
    public ApiResponse<?> updateLeave(@RequestBody UpdateLeaveRequest request,@RequestParam String approvedEmployeeId);
}
