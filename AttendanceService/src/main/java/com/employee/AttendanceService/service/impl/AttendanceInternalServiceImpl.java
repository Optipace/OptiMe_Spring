package com.employee.AttendanceService.service.impl;

import com.employee.AttendanceService.client.EmployeeClient;
import com.employee.AttendanceService.client.LeaveClient;
import com.employee.AttendanceService.dto.response.*;
import com.employee.AttendanceService.enums.AttendanceStatusEnum;
import com.employee.AttendanceService.enums.CustomStatus;
import com.employee.AttendanceService.exception.CustomException;
import com.employee.AttendanceService.model.Attendance;
import com.employee.AttendanceService.repository.AttendanceRepository;
import com.employee.AttendanceService.service.AttendanceInternalService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Slf4j
public class AttendanceInternalServiceImpl implements AttendanceInternalService {

    private final AttendanceRepository attendanceRepository;

    private final EmployeeClient employeeClient;

    private final ObjectMapper objectMapper;

    private final LeaveClient leaveClient;

    @Override
    public ApiResponse<?> getAttendanceStatus(String employeeId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        String finalStatus;

        // 1. Check Leave Microservice first (or your fallback logic)
        if (isEmployeeOnLeaveInMicroservice(employeeId, today)) {
            finalStatus = AttendanceStatusEnum.ON_LEAVE.toString();
            return new ApiResponse<>("Attendance Status", finalStatus, 200);
        }else {
            // 2. Query today's local attendance record
            Optional<List<Attendance>> todayAttendance = attendanceRepository.findTodayAttendanceByEmployeeId(employeeId, startOfDay, endOfDay);
//        // 3. Safely unwrap, verify it's not empty, and map the internal collection
            finalStatus = todayAttendance
                    .filter(list -> !list.isEmpty())
                    .map(list -> list.getLast().getAttendanceStatus().name())
                    .orElse(null);
        }

//        AttendanceStatusResponse attendanceResponse = finalStatus != null ? new AttendanceStatusResponse(String.valueOf(finalStatus)) : null;

//      use find today attendance and place if the attendance is online return present (rename attendance status to status and make another status as attendance status) if offline return left the office if no records found search in the leave service

        // 2. Query today's local attendance record
//        Optional<List<Attendance>> todayAttendance = attendanceRepository.findTodayAttendanceByEmployeeId(employeeId, startOfDay, endOfDay);

//        String finalStatus = todayAttendance
//                .filter(list -> !list.isEmpty())
//                .map(list -> list.getLast().getAttendanceStatus().name())
//                .orElse(null);

        log.info("Attendance service returning status {}", finalStatus);

        return new ApiResponse<>("Attendance Status", finalStatus, 200);
    }

    private boolean isEmployeeOnLeaveInMicroservice(String employeeId, LocalDate date) {
         return leaveClient.isEmployeeOnLeave(employeeId, date);
    }
    @Override
    public ApiResponse<?> getTodayAttendanceRecords() {
        ListOfEmployeeIdResponse listOfEmployeeIds;
        try {
            ApiResponse<ListOfEmployeeIdResponse> apiResponse = employeeClient.getAllEmployeeId();
            listOfEmployeeIds = apiResponse.getData();
        } catch (FeignException fe) {
            String rawErrorJson = fe.contentUTF8();
            String cleanErrorMessage = "Microservices call failed";

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").toString();
                } else {
                    cleanErrorMessage = rawErrorJson;
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if (fe.status() > 0) {
                try {
                    responseStatus = HttpStatus.valueOf(fe.status());
                } catch (IllegalArgumentException ex) {
                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            } else {
                cleanErrorMessage = "Service is unreachable. Please try again later.";
                responseStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503 Status
            }
            throw new CustomException(cleanErrorMessage, responseStatus);
        }

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

// 1. Fetch ALL records from DB sorted chronologically
        List<Attendance> attendanceList = attendanceRepository.findByCheckInTimeBetweenOrderByCheckInTimeAsc(startOfDay, endOfDay);

// 2. Initialize the final response list and a basic set to track who checked in at least once
        List<EmployeeAttendanceResponse> responseList = new ArrayList<>();
        Set<String> employeesWhoCheckedIn = new HashSet<>();

// 3. Step 1: Add EVERY attendance record found in the DB (Allowing multiple entries per employee)
        for (Attendance attendance : attendanceList) {
            employeesWhoCheckedIn.add(attendance.getEmployeeId()); // Tracks that this employee is present today

            responseList.add(new EmployeeAttendanceResponse(
                    attendance.getEmployeeId(),
                    attendance.getCheckInTime(),
                    attendance.getCheckOutTime(),
                    attendance.getLatitude(),
                    attendance.getLongitude(),
                    attendance.getAttendanceStatus().toString(),
                    attendance.getAttendanceTypeId()
            ));
        }

// 4. Step 2: Look at all company IDs and append employees who have ZERO records today to the bottom
        List<String> allEmpIds = listOfEmployeeIds.getEmployeeIds();
        for (String empId : allEmpIds) {
            if (!employeesWhoCheckedIn.contains(empId)) {
                responseList.add(new EmployeeAttendanceResponse(
                        empId,
                        null,
                        null,
                        null,
                        null,
                        "ABSENT", // TODO: need to update to getAttendanceStatus if he/she is in leave
                        null
                ));
            }
        }

// responseList now contains the full timeline of events, followed by absent employees!

// responseList now contains sorted present employees first, and absent employees last!

        return new ApiResponse<>("Attendance Records", responseList, 200);
    }
}
