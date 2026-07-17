package com.employee.LeaveService.controller;

import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.LeaveTypeResponse;
import com.employee.LeaveService.service.LeaveInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
