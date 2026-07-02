package com.employee.LeaveService.controller;

import com.employee.LeaveService.dto.request.LeaveRequest;
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

    @PostMapping("/applyLeave")
    public ResponseEntity<ApiResponse<?>> saveLeaveApplication(@RequestHeader("X-Employee-Id") String employeeId,@Valid @RequestBody LeaveRequest request){
        ApiResponse<?> response = leaveService.saveLeaveApplication(request, employeeId);
        return ResponseEntity.status(200).body(response);
    }
}
