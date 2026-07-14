package com.employee.AttendanceService.controller;

import com.employee.AttendanceService.dto.response.*;
import com.employee.AttendanceService.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.employee.AttendanceService.enums.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/checkIn")
    public ResponseEntity<ApiResponse<?>> employeeCheckIn(@RequestHeader("X-Employee-Id") String employeeId,
                                                          @RequestParam(value = "image", required = false) MultipartFile file,
                                                          @RequestParam("latitude") String latitude,
                                                          @RequestParam("longitude") String longitude,
                                                          @RequestParam("attendanceType")WorkTypeEnum attendanceType){
        ApiResponse<?> response = attendanceService.employeeCheckIn(employeeId, file, latitude, longitude, attendanceType);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/checkOut")
    public ResponseEntity<ApiResponse<?>> employeeCheckOut(@RequestHeader("X-Employee-Id") String employeeId){
        ApiResponse<?> response = attendanceService.employeeCheckOut(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getWorkingDetails")
    public ApiResponse<WorkingDetailsResponse> getWorkingDetails(@RequestHeader("X-Employee-Id") String employeeId){
        return attendanceService.getWorkingDetails(employeeId);
    }
}