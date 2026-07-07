package com.employee.AttendanceService.controller;

import com.employee.AttendanceService.dto.response.ApiResponse;
import com.employee.AttendanceService.service.AttendanceInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendance/internal")
@RequiredArgsConstructor
public class AttendanceInternalController {
    private final AttendanceInternalService internalService;

    @GetMapping("/getAttendanceStatus")
    public ResponseEntity<ApiResponse<?>> getAttendanceStatus(@RequestParam String employeeId){
        ApiResponse<?> response = internalService.getAttendanceStatus(employeeId);
        return ResponseEntity.status(200).body(response);
    }
}
