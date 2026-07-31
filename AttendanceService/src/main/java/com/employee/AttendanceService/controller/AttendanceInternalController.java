package com.employee.AttendanceService.controller;

import com.employee.AttendanceService.dto.request.DateWiseAttendanceRequest;
import com.employee.AttendanceService.dto.response.ApiResponse;
import com.employee.AttendanceService.dto.response.EmployeeAttendanceHistoryResponse;
import com.employee.AttendanceService.service.AttendanceInternalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/getTodayAttendanceRecords")
    public ResponseEntity<ApiResponse<?>> getTodayAttendanceRecords(){
        ApiResponse<?> response = internalService.getTodayAttendanceRecords();
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/getDateWiseAttendanceRecords")
    public ResponseEntity<ApiResponse<List<EmployeeAttendanceHistoryResponse>>> getDateWiseAttendanceRecords(@Valid @RequestBody DateWiseAttendanceRequest request){
        ApiResponse<List<EmployeeAttendanceHistoryResponse>> response = internalService.getDateWiseAttendanceRecords(request);
        return ResponseEntity.status(200).body(response);
    }
}
