package com.employee.LeaveService.controller;

import com.employee.LeaveService.dto.request.UpdateLeaveRequest;
import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.LeaveTypeResponse;
import com.employee.LeaveService.service.LeaveInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave/internal")
@RequiredArgsConstructor
public class LeaveInternalController {

    private final LeaveInternalService leaveInternalService;

    @GetMapping("/getAllAppliedLeaves")
    public ApiResponse<?> getAllAppliedLeaves(){
        return leaveInternalService.getAllAppliedLeaves();
    }

    @GetMapping("/getLeaveTypeList")
    public ApiResponse<List<LeaveTypeResponse>> getLeaveTypeList(){
        return leaveInternalService.getLeaveTypeList();
    }

    @GetMapping("/isEmployeeOnLeave")
    public boolean isEmployeeOnLeave(@RequestParam("employeeId") String employeeId,@RequestParam("today") LocalDate today){
        return leaveInternalService.isEmployeeOnLeave(employeeId, today);
    }

    @PutMapping("/leaveApproval")
    public ApiResponse<?> updateLeave(@RequestBody UpdateLeaveRequest request,@RequestParam String approvedEmployeeId){
        return leaveInternalService.updateLeave(request, approvedEmployeeId);
    }
}
