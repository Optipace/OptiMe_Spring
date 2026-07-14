package com.employee.AttendanceService.service;

import com.employee.AttendanceService.dto.request.*;
import com.employee.AttendanceService.dto.response.*;
import com.employee.AttendanceService.enums.WorkTypeEnum;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttendanceService {

    public ApiResponse<?> employeeCheckIn(String request, MultipartFile file, String latitude, String longitude, WorkTypeEnum attendanceType);

    public ApiResponse<?> employeeCheckOut(String request);

    public ApiResponse<WorkingDetailsResponse> getWorkingDetails(String employeeId);
}
