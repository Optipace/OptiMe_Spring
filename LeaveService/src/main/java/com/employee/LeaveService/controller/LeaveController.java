package com.employee.LeaveService.controller;

import com.employee.LeaveService.dto.request.LeaveRequest;
import com.employee.LeaveService.dto.request.UpdateLeaveRequest;
import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // TODO : Need to add authority EMPid
    @PostMapping("/applyLeave")
    public ResponseEntity<ApiResponse<?>> saveLeaveApplication(@RequestHeader("X-Employee-Id") String employeeId,@Valid @RequestBody LeaveRequest request, @RequestHeader("X-Employee-Name") String employeeName){
        ApiResponse<?> response = leaveService.saveLeaveApplication(request, employeeId, employeeName);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/approval")
    public ResponseEntity<ApiResponse<?>> updateLeave(@RequestBody UpdateLeaveRequest request,
                                                      @RequestHeader("X-Employee-Id") String approvedEmployeeId){
        ApiResponse<?> response = leaveService.updateLeave(request, approvedEmployeeId);
        return ResponseEntity.status(200).body(response);
    }
}
