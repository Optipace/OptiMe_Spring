package com.employee.AttendanceService.service.impl;

import com.employee.AttendanceService.dto.response.*;
import com.employee.AttendanceService.enums.AttendanceStatusEnum;
import com.employee.AttendanceService.exception.CustomException;
import com.employee.AttendanceService.model.*;
import com.employee.AttendanceService.repository.AttendanceRepository;
import com.employee.AttendanceService.service.AttendanceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final ModelMapper mapperModel;

    private final AttendanceRepository attendanceRepository;

//    private final EmployeeClient employeeClient;

//    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ApiResponse<?> employeeCheckIn(String employeeId) {

        boolean isAlreadyCheckedIn = attendanceRepository.existsByEmployeeIdAndCheckOutTimeIsNull(employeeId);

        if(isAlreadyCheckedIn)
            throw new CustomException("You are already checked in. Please check out first.", HttpStatus.BAD_REQUEST);

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setCheckOutTime(null);
        attendance.setTotalWorkMin(0L);
        attendance.setAttendanceStatus(AttendanceStatusEnum.ONLINE);
        attendanceRepository.save(attendance);

//        try{
//            UpdateEmployeeStatusPayload payload = new UpdateEmployeeStatusPayload();
//            payload.setEmployeeId(attendance.getEmployeeId());
//            payload.setEmployeeStatus(EmployeeStatusEnum.valueOf(String.valueOf(attendance.getAttendanceStatus())));
//            employeeClient.updateEmployeeStatus(payload);
//            log.info("Employee service called after check-in");
//        }catch (FeignException fe){
//            String rawErrorJson = fe.contentUTF8();
//            String cleanErrorMessage = "Microservices failed";
//
//            try {
//                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
//                if (errorNode.has("message")) {
//                    cleanErrorMessage = errorNode.get("message").asString();
//                } else {
//                    cleanErrorMessage = rawErrorJson;
//                }
//            } catch (Exception parseException) {
//                cleanErrorMessage = rawErrorJson;
//            }
//            // Resolve status code safely.
//            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//            if (fe.status() > 0) {
//                try {
//                    responseStatus = HttpStatus.valueOf(fe.status());
//                } catch (IllegalArgumentException ex) {
//                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//                }
//            } else {
//                cleanErrorMessage = "Service is unreachable. Please try again later.";
//                responseStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503 Status
//                log.error("Employee profile service unavailable");
//            }
//            throw new CustomException(cleanErrorMessage, responseStatus);
//        }
        return new ApiResponse<>(
                "Login Successful",
                null,
                HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ApiResponse<?> employeeCheckOut(String employeeId){
        Attendance attendance = attendanceRepository.findByEmployeeIdAndCheckOutTimeIsNull(employeeId)
                .orElseThrow(() -> new CustomException("No active check-in record found for this employee", HttpStatus.NOT_FOUND));

        attendance.setTotalWorkMin(Duration.between(attendance.getCheckInTime(), LocalDateTime.now()).toMinutes());

        if(attendance.getCheckOutTime() == null)
            attendance.setCheckOutTime(LocalDateTime.now());

        attendance.setAttendanceStatus(AttendanceStatusEnum.OFFLINE);
        attendanceRepository.save(attendance);
//        try{
//            UpdateEmployeeStatusPayload payload = new UpdateEmployeeStatusPayload(
//                    attendance.getEmployeeId(),
//                    EmployeeStatusEnum.valueOf(String.valueOf(attendance.getAttendanceStatus()))
//            );
//            employeeClient.updateEmployeeStatus(payload);
//            log.info("Employee service called after check-out");
//        }catch (FeignException fe){
//            String rawErrorJson = fe.contentUTF8();
//            String cleanErrorMessage = "Micro-Services failed";
//            try{
//                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
//                if(errorNode.has("message")){
//                    cleanErrorMessage = errorNode.get("message").toString();
//                }else{
//                    cleanErrorMessage = rawErrorJson;
//                }
//            }catch (Exception parseException) {
//                cleanErrorMessage = rawErrorJson;
//            }
//            // Resolve status code safely.
//            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//            if (fe.status() > 0) {
//                try {
//                    responseStatus = HttpStatus.valueOf(fe.status());
//                } catch (IllegalArgumentException ex) {
//                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//                }
//            } else {
//                cleanErrorMessage = "Service is unreachable. Please try again later.";
//                responseStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503 Status
//                log.error("Employee profile service unavailable");
//            }
//            throw new CustomException(cleanErrorMessage, responseStatus);
//        }
        return new ApiResponse<>(
                "Logout Successful",
                null,
                HttpStatus.OK
        );
    }

//    @Override

//    public ApiResponse<?> getTotalWorkMin(TotalWorkMinRequest request) {
//        int currentWeek = LocalDateTime.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
//        int currentYear = LocalDateTime.now().get(IsoFields.WEEK_BASED_YEAR);
//
//        Employee employee = employeeRepository.findEmployeeByEmployeeId(request.getEmployeeId())
//                .orElseThrow(() -> new CustomException("Employee records not found", HttpStatus.NOT_FOUND));
//
//        Long totalWorkMin = attendanceRepository.getTotalWorkMin(employee.getId(),currentWeek,currentYear)
//                .orElse(0L);
//
//        return new ApiResponse<>(
//                true,
//                "Total weekly work minutes for employee = "+employee.getEmployeeId(),
//                totalWorkMin,
//                LocalDateTime.now(),
//                HttpStatus.OK
//        );
//    }

//    @Override
//    public ApiResponse<?> getTotalWorkMin(TotalWorkMinRequest request){
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime fromDate = LocalDateTime.parse(request.getFromDate(), formatter);
//        LocalDateTime toDate = LocalDateTime.parse(request.getToDate(), formatter);
//        Employee employee = employeeRepository.findEmployeeByEmployeeId(request.getEmployeeId())
//                .orElseThrow(() -> new CustomException("Employee records not found", HttpStatus.NOT_FOUND));
//
//        Long totalWorkMin = attendanceRepository.getTotalWorkMin(employee.getId(), fromDate, toDate)
//                        .orElse(0L);
//
//
//        System.out.println("Total Work minutes = "+totalWorkMin);
//        return new ApiResponse<>(
//                true,
//                "Total weekly work minutes for employee = "+employee.getEmployeeId(),
//                totalWorkMin,
//                LocalDateTime.now(),
//                HttpStatus.OK
//        );
//    }

    @Override
    public ApiResponse<WorkingDetailsResponse> getWorkingDetails(String employeeId){
        LocalDateTime fromDate = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .with(LocalTime.MIN);
        LocalDateTime toDate = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
                .with(LocalTime.MAX);

        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

        List<Attendance> attendanceList = attendanceRepository.findTodayAttendanceByEmployeeId(employeeId,startOfDay,endOfDay)
                .orElseThrow(() -> new CustomException("No attendance records found", HttpStatus.NOT_FOUND));

        Long totalWorkMin = attendanceRepository.getTotalWorkMin(employeeId, fromDate, toDate)
                .orElse(0L);

        List<AttendanceResponse> logResponse = attendanceList.stream()
                .map(attendance -> mapperModel.map(attendance, AttendanceResponse.class))
                .toList();

        WorkingDetailsResponse response = new WorkingDetailsResponse(totalWorkMin, logResponse);

        return new ApiResponse<>(
                "Total working details",
                response,
                HttpStatus.OK
        );
    }
}
