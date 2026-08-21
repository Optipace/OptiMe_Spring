package com.employee.AdminService.client;

import com.employee.AdminService.dto.request.ApproveLeaveRequest;
import com.employee.AdminService.dto.request.RejectLeaveRequest;
import com.employee.AdminService.dto.request.UpdateLeaveRequest;
import com.employee.AdminService.dto.response.*;
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
    SingleResponse<List<ListOfLeaveResponse>> getAllAppliedLeaves();

    @GetMapping("/api/leave/internal/getLeaveTypeList")
    public SingleResponse<List<LeaveTypeResponse>> getLeaveTypeList();

    @PutMapping("/api/leave/internal/approveLeave")
    public SingleResponse<?> approveLeave(@RequestBody ApproveLeaveRequest request, @RequestParam String authorityEmployeeId);

    @PutMapping("/api/leave/internal/rejectLeave")
    public SingleResponse<?> rejectLeave(@RequestBody RejectLeaveRequest request, @RequestParam String authorityEmployeeId);
}
