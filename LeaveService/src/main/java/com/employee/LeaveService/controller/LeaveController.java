package com.employee.LeaveService.controller;

import com.employee.LeaveService.dto.request.ApproveLeaveRequest;
import com.employee.LeaveService.dto.request.CancelMyLeaveRequest;
import com.employee.LeaveService.dto.request.LeaveRequest;
import com.employee.LeaveService.dto.request.RejectLeaveRequest;
import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping("/applyLeave")
    public ResponseEntity<SingleResponse<?>> saveLeaveApplication(@RequestHeader("X-Employee-Id") String applicantEmployeeId,
                                                                  @Valid @RequestBody LeaveRequest request,
                                                                  @RequestHeader("X-Employee-Name") String applicantEmployeeName,
                                                                  @RequestHeader("X-Email-Id")String applicantEmailId){
        SingleResponse<?> response = leaveService.saveLeaveApplication(request, applicantEmployeeId, applicantEmployeeName, applicantEmailId);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/approveLeave")
    public ResponseEntity<SingleResponse<?>> approveLeave(@Valid @RequestBody ApproveLeaveRequest request,
                                                          @RequestHeader("X-Employee-Id") String authorityEmployeeId){
        SingleResponse<?> response = leaveService.approveLeave(request, authorityEmployeeId);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/rejectLeave")
    public ResponseEntity<SingleResponse<?>> rejectLeave(@Valid @RequestBody RejectLeaveRequest request,
                                                         @RequestHeader("X-Employee-Id") String authorityEmployeeId){
        SingleResponse<?> response = leaveService.rejectLeave(request, authorityEmployeeId);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/myAppliedLeaves")
    public ResponseEntity<SingleResponse<?>> getMyAppliedLeaves(@RequestHeader("X-Employee-Id")String employeeId){
        SingleResponse<?> response = leaveService.getMyAppliedLeaves(employeeId);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/cancelMyLeave")
    public ResponseEntity<SingleResponse<?>> cancelMyLeave(@RequestBody CancelMyLeaveRequest request,
                                                           @RequestHeader("X-Employee-Id") String employeeId){
        SingleResponse<?> response = leaveService.cancelMyLeave(request,employeeId);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getAppliedLeavesForMe")
    public ResponseEntity<SingleResponse<?>> getAppliedLeavesForMe(@RequestHeader("X-Employee-Id") String employeeId){
        return ResponseEntity.status(200).body(leaveService.getAppliedLeavesForMe(employeeId));
    }

    @GetMapping("/getAllPendingLeaves")
    public ResponseEntity<SingleResponse<?>> getAllPendingLeaves(){
        return ResponseEntity.status(200).body(leaveService.getAllPendingLeaves());
    }

    @GetMapping("/getProcessedLeaves")
    public ResponseEntity<SingleResponse<?>> getAllProcessedLeaves(){
        return ResponseEntity.status(200).body(leaveService.getAllProcessedLeaves());
    }

}
