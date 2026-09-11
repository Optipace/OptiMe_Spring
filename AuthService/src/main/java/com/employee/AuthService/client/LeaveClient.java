package com.employee.AuthService.client;

import com.employee.AuthService.dto.request.AvailableLeavesPayload;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.dto.response.AvailableLeavesResponse;
import com.employee.AuthService.dto.response.LeaveTypeResponse;
import com.employee.AuthService.dto.response.SingleResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "LEAVE-SERVICE")
public interface LeaveClient {
    @GetMapping("/api/leave/internal/getLeaveTypeList")
    public SingleResponse<List<LeaveTypeResponse>> getLeaveTypeList();

    @PostMapping("/api/leave/internal/save/availableLeaves")
    public SingleResponse<AvailableLeavesResponse> saveAvailableLeaves(@Valid @RequestBody AvailableLeavesPayload payload);

    @DeleteMapping("/api/leave/internal/delete/availableLeaves")
    public SingleResponse<?> deleteAvailableLeaves(@RequestParam Long id);
}
