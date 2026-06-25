package com.employee.AttendanceService.controller;

import com.employee.AttendanceService.dto.response.*;
import com.employee.AttendanceService.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/checkIn")
    public ResponseEntity<ApiResponse<?>> employeeCheckIn(@RequestHeader("X-Employee-Id") String employeeId){
        ApiResponse<?> response = attendanceService.employeeCheckIn(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/checkOut")
    public ResponseEntity<ApiResponse<?>> employeeCheckOut(@RequestHeader("X-Employee-Id") String employeeId){
        ApiResponse<?> response = attendanceService.employeeCheckOut(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getWorkingDetails")
    public ResponseEntity<ApiResponse<WorkingDetailsResponse>> getWorkingDetails(@RequestHeader("X-Employee-Id") String employeeId){
        ApiResponse<WorkingDetailsResponse> response = attendanceService.getWorkingDetails(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}