package com.employee.LeaveService.controller;

import com.employee.LeaveService.dto.request.LeaveRequest;
import com.employee.LeaveService.dto.request.UpdateLeaveRequest;
import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.SingleResponse;
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

    @PostMapping("/applyLeave")
    public ResponseEntity<SingleResponse<?>> saveLeaveApplication(@RequestHeader("X-Employee-Id") String applicantEmployeeId,@Valid @RequestBody LeaveRequest request, @RequestHeader("X-Employee-Name") String applicantEmployeeName){
        SingleResponse<?> response = leaveService.saveLeaveApplication(request, applicantEmployeeId, applicantEmployeeName);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/approval")
    public ResponseEntity<SingleResponse<?>> updateLeave(@RequestBody UpdateLeaveRequest request,
                                                      @RequestHeader("X-Employee-Id") String approvedEmployeeId){
        SingleResponse<?> response = leaveService.updateLeave(request, approvedEmployeeId);
        return ResponseEntity.status(200).body(response);
    }
}
