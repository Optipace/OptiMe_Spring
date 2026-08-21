package com.employee.AttendanceService.controller;

import com.employee.AttendanceService.dto.request.DateWiseAttendanceRequest;
import com.employee.AttendanceService.dto.request.UpdateCheckOutRecordsRequest;
import com.employee.AttendanceService.dto.response.ApiResponse;
import com.employee.AttendanceService.dto.response.EmployeeAttendanceHistoryResponse;
import com.employee.AttendanceService.dto.response.SingleResponse;
import com.employee.AttendanceService.dto.response.WeeklyAttendanceLogsOfEmployeeRes;
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
    public ResponseEntity<SingleResponse<?>> getAttendanceStatus(@RequestParam Long employeeId){
        SingleResponse<?> response = internalService.getAttendanceStatus(employeeId);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getTodayAttendanceRecords")
    public ResponseEntity<SingleResponse<?>> getTodayAttendanceRecords(){
        SingleResponse<?> response = internalService.getTodayAttendanceRecords();
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/getDateWiseAttendanceRecords")
    public SingleResponse<List<EmployeeAttendanceHistoryResponse>> getDateWiseAttendanceRecords(@Valid @RequestBody DateWiseAttendanceRequest request){
        return internalService.getDateWiseAttendanceRecords(request);
    }

    @GetMapping("/getWeeklyAttendanceLogs")
    public ResponseEntity<SingleResponse<WeeklyAttendanceLogsOfEmployeeRes>> getWeeklyAttendanceLogs(@RequestParam Long employeeId){
        SingleResponse<WeeklyAttendanceLogsOfEmployeeRes> response = internalService.getWeeklyAttendanceLogs(employeeId);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/updateCheckOut")
    public ResponseEntity<ApiResponse<?>> updateCheckoutRecordsByEmployeeId(@RequestBody UpdateCheckOutRecordsRequest request){
        return ResponseEntity.status(200).body(internalService.updateCheckoutRecordsByEmpId(request));
    }
}
