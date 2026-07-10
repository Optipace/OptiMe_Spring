package com.employee.AdminService.controller;

import com.employee.AdminService.dto.request.NotificationPayload;
import com.employee.AdminService.dto.request.NotificationRequest;
import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/getAllAppliedLeaves")
    public ResponseEntity<ApiResponse<?>> getAllAppliedLeaves(){
        ApiResponse<?> response = employeeService.getAllAppliedLeaves();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/messageForEmployees")
    public ResponseEntity<ApiResponse<?>> sendBroadcastMessage(@RequestBody NotificationRequest request){
        ApiResponse<?> response = employeeService.sendBroadcastMessage(request);
        return ResponseEntity.ok(response);
    }
}
