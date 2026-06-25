package com.employee.AttendanceService.service.impl;

import com.employee.AttendanceService.dto.response.ApiResponse;
import com.employee.AttendanceService.dto.response.AttendanceStatusResponse;
import com.employee.AttendanceService.enums.AttendanceStatusEnum;
import com.employee.AttendanceService.exception.CustomException;
import com.employee.AttendanceService.model.Attendance;
import com.employee.AttendanceService.repository.AttendanceRepository;
import com.employee.AttendanceService.service.AttendanceInternalService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class AttendanceInternalServiceImpl implements AttendanceInternalService {

    private final AttendanceRepository attendanceRepository;
    @Override
    public ApiResponse<?> getAttendanceStatus(String employeeId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

//        AttendanceStatusResponse response = null;
        // 1. Check Leave Microservice first (or your fallback logic)
//        if (isEmployeeOnLeaveInMicroservice(employeeId, today)) {
//            response.setAttendanceStatus(AttendanceStatusEnum.ON_LEAVE);
//        }

        // 2. Query today's local attendance record
//        Optional<List<Attendance>> todayAttendance = attendanceRepository.findTodayAttendanceByEmployeeId(employeeId, startOfDay, endOfDay);
//
//        // 3. Safely unwrap, verify it's not empty, and map the internal collection
//        AttendanceStatusEnum finalStatus = todayAttendance
//                .filter(list -> !list.isEmpty())
//                .map(list -> list.getLast().getAttendanceStatus().name())
//                .orElse(null);
//
//        AttendanceStatusResponse attendanceResponse = finalStatus != null ? new AttendanceStatusResponse(String.valueOf(finalStatus)) : null;

//use find today attendance and place if the attendance is online return present (rename attendance status to status and make another status as attendance status if offline return left the office if no records found search in the leave service
        // 2. Query today's local attendance record
        Optional<List<Attendance>> todayAttendance = attendanceRepository.findTodayAttendanceByEmployeeId(employeeId, startOfDay, endOfDay);
//
//        // 3. Safely unwrap, verify it's not empty, and map the internal collection
        String finalStatus = todayAttendance
                .filter(list -> !list.isEmpty())
                .map(list -> list.getLast().getAttendanceStatus().name())
                .orElse(null);
//        if(finalStatus == null)
//            finalStatus = "null";

        return new ApiResponse<>(
                true,
                "Attendance Status",
                finalStatus,
                LocalDateTime.now(),
                200
        );
    }

    /**
     * Placeholder for your future Leave Microservice integration.
     * You will eventually replace this mock logic with WebClient, OpenFeign, or gRPC.
     */
//    private boolean isEmployeeOnLeaveInMicroservice(String employeeId, LocalDate date) {
//        // TODO: In the future, call Leave Microservice here.
//        // Example: return leaveServiceClient.isEmployeeOnLeave(employeeId, date);
//        return false;
//    }
}
