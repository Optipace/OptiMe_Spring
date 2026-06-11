package com.employee.AttendanceService.controller;

import com.employee.AttendanceService.dto.request.*;
import com.employee.AttendanceService.dto.response.*;
import com.employee.AttendanceService.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emp/")
@RequiredArgsConstructor
public class EmployeeController {

    private final AttendanceService attendanceService;

    @PostMapping("/checkIn")
    public ResponseEntity<ApiResponse<?>> employeeCheckIn(@Valid @RequestBody EmployeeLoginRequest request){
        ApiResponse<?> response = attendanceService.employeeLogin(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/checkOut")
    public ResponseEntity<ApiResponse<?>> employeeCheckOut(@Valid @RequestBody EmployeeLogoutRequest request){
        ApiResponse<?> response = attendanceService.employeeLogout(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getWorkingDetails")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getWorkingDetails(@RequestParam String employeeId){
        ApiResponse<List<AttendanceResponse>> response = attendanceService.getWorkingDetails(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}