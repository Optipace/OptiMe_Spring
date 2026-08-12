package com.employee.LeaveService.controller;

import com.employee.LeaveService.dto.request.ApproveLeaveRequest;
import com.employee.LeaveService.dto.request.RejectLeaveRequest;
import com.employee.LeaveService.dto.request.UpdateLeaveRequest;
import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.LeaveTypeResponse;
import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.service.LeaveInternalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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

//    @PutMapping("/leaveApproval")
//    public ApiResponse<?> updateLeave(@RequestBody UpdateLeaveRequest request,@RequestParam String approvedEmployeeId){
//        return leaveInternalService.updateLeave(request, approvedEmployeeId);
//    }

    @PutMapping("/approveLeave")
    public ApiResponse<?> approveLeave(@Valid @RequestBody ApproveLeaveRequest request,
                                       @RequestParam String authorityEmployeeId){
        return leaveInternalService.approveLeave(request, authorityEmployeeId);
    }

    @PutMapping("/rejectLeave")
    public ApiResponse<?> rejectLeave(@Valid @RequestBody RejectLeaveRequest request,
                                      @RequestParam String authorityEmployeeId){
        return leaveInternalService.rejectLeave(request, authorityEmployeeId);
    }

    @GetMapping("/leaveDates/{employeeId}")
    public ApiResponse<Set<LocalDate>> getEmployeeLeaveDatesInRange(
            @PathVariable("employeeId") String employeeId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate
    ){
        return leaveInternalService.getEmployeeLeaveDatesInRange(employeeId, startDate, endDate);
    }
}
