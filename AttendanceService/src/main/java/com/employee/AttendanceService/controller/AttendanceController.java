package com.employee.AttendanceService.controller;

import com.employee.AttendanceService.dto.request.DateWiseAttendanceRequest;
import com.employee.AttendanceService.dto.response.*;
import com.employee.AttendanceService.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.employee.AttendanceService.enums.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/checkIn")
    public ResponseEntity<SingleResponse<?>> employeeCheckIn(@RequestHeader("X-Employee-Id") String employeeId,
                                                          @RequestParam(value = "image", required = false) MultipartFile file,
                                                          @RequestParam("latitude") String latitude,
                                                          @RequestParam("longitude") String longitude,
                                                          @RequestParam("attendanceTypeId")Long attendanceTypeId){
        SingleResponse<?> response = attendanceService.employeeCheckIn(employeeId, file, latitude, longitude, attendanceTypeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/checkOut")
    public ResponseEntity<SingleResponse<?>> employeeCheckOut(@RequestHeader("X-Employee-Id") String employeeId){
        SingleResponse<?> response = attendanceService.employeeCheckOut(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getWorkingDetails")
    public SingleResponse<WorkingDetailsResponse> getWorkingDetails(@RequestHeader("X-Employee-Id") String employeeId){
        return attendanceService.getWorkingDetails(employeeId);
    }

    @GetMapping("/getWeeklyAttendanceLogs")
    public ResponseEntity<SingleResponse<WeeklyAttendanceLogsOfEmployeeRes>> getWeeklyAttendanceLogs(@RequestHeader("X-Employee-Id") String employeeId){
        SingleResponse<WeeklyAttendanceLogsOfEmployeeRes> response = attendanceService.getWeeklyAttendanceLogs(employeeId);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getTodayAttendanceRecords")
    public ResponseEntity<SingleResponse<?>> getTodayAttendanceRecords(){
        SingleResponse<?> response = attendanceService.getTodayAttendanceRecords();
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/getDateWiseAttendanceRecords")
    public ResponseEntity<SingleResponse<List<EmployeeAttendanceHistoryResponse>>> getDateWiseAttendanceRecords(@Valid @RequestBody DateWiseAttendanceRequest request){
        SingleResponse<List<EmployeeAttendanceHistoryResponse>> response = attendanceService.getDateWiseAttendanceRecords(request);
        return ResponseEntity.status(200).body(response);
    }
}