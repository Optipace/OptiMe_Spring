package com.employee.AttendanceService.service.impl;

import com.employee.AttendanceService.client.EmployeeClient;
import com.employee.AttendanceService.client.LeaveClient;
import com.employee.AttendanceService.dto.request.DateWiseAttendanceRequest;
import com.employee.AttendanceService.dto.request.UpdateCheckOutRecordsRequest;
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
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
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

    private final ModelMapper modelMapper;

    @Override
    public SingleResponse<?> getAttendanceStatus(Long employeeId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        LocalTime cutOffTime = LocalTime.of(12, 0);
        LocalTime now = LocalTime.now();
        String attendanceStatus;

        // Check Leave Microservice first
        if (isEmployeeOnLeaveInMicroservice(employeeId, today)) {
            attendanceStatus = AttendanceStatusEnum.ON_LEAVE.toString();
            return new SingleResponse<>(
                    attendanceStatus,
                    CustomStatus.SUCCESS
                    );
        }else {

            Optional<List<Attendance>> todayAttendance = attendanceRepository.findTodayAttendanceByEmployeeId(employeeId, startOfDay, endOfDay);

            attendanceStatus = todayAttendance
                    .filter(list -> !list.isEmpty())
                    .map(list -> list.getLast().getAttendanceStatus().name())
                    .orElseGet(() -> {
                        // This block ONLY runs if the employee has zero attendance records today
                        if (!now.isBefore(cutOffTime)) {
                            return AttendanceStatusEnum.ABSENT.name(); // Past 12:00 PM -> Mark ABSENT
                        }
                        return null; // Before 12:00 PM -> Keep it null
                    });
        }

        log.info("Attendance service returning status {}", attendanceStatus);

        return new SingleResponse<>(
                attendanceStatus,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> getTodayAttendanceRecords() {
        ListOfEmployeeIdResponse listOfEmployeeIds;
        try {
            SingleResponse<ListOfEmployeeIdResponse> apiResponse = employeeClient.getAllEmployeeId();
            listOfEmployeeIds = apiResponse.getData();
        } catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";
            int extractedErrorCode = -100; // Defaults to MICROSERVICE_CALL_FAILED code

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);

                // Navigate inside the nested "response" block
                if (errorNode.has("response")) {
                    JsonNode responseNode = errorNode.get("response");
                    if (responseNode.has("message")) {
                        cleanErrorMessage = responseNode.get("message").asText();
                    }
                    if (responseNode.has("code")) {
                        extractedErrorCode = responseNode.get("code").asInt();
                    }
                } else if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            int httpStatusValue = (e.status() > 0) ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();

            // Map the integer code to the correct Enum instance safely
            CustomStatus status = CustomStatus.fromCode(extractedErrorCode);

            // Pass the clean extracted message to CustomException
            throw new CustomException(cleanErrorMessage, status, httpStatusValue);
        }

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        LocalTime cutOffTime = LocalTime.of(12,0);
        LocalTime now = LocalTime.now();

        // 1. Fetch ALL records from DB sorted chronologically
        List<Attendance> attendanceList = attendanceRepository.findByCheckInTimeBetweenOrderByCheckInTimeAsc(startOfDay, endOfDay);

        // 2. Initialize the final response list and a basic set to track who checked in at least once
        List<EmployeeAttendanceResponse> responseList = new ArrayList<>();
        Set<Long> employeesWhoCheckedIn = new HashSet<>();

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
                    attendance.getAttendanceTypeId(),
                    getWorkingDetailsOfEmployee(attendance.getEmployeeId())
            ));
        }

        // 4. Step 2: Look at all IDs and append employees who have ZERO records today to the bottom
        List<Long> allEmpIds = listOfEmployeeIds.getEmployeeIds();
        for (Long empId : allEmpIds) {
            if (!employeesWhoCheckedIn.contains(empId)) {
                String attendanceStatus = null; // Default status

                if(!now.isBefore(cutOffTime)){
                    attendanceStatus = AttendanceStatusEnum.ABSENT.toString();
                }
                try {
                    boolean isOnLeave = leaveClient.isEmployeeOnLeave(empId, today);// TODO: can make bulk api call to load leave of emp all at once
                    if (isOnLeave) {
                        attendanceStatus = AttendanceStatusEnum.ON_LEAVE.toString();
                    }
                } catch (FeignException e) {
                    log.error("Leave service call failed for employee: {}", empId, e);
                    // Defaulting to ABSENT if the service fails or throws exception
                    String rawErrorJson = e.contentUTF8();
                    String cleanErrorMessage = "Microservice call failed";
                    int extractedErrorCode = -100; // Defaults to MICROSERVICE_CALL_FAILED code

                    try {
                        JsonNode errorNode = objectMapper.readTree(rawErrorJson);

                        // Navigate inside the nested "response" block
                        if (errorNode.has("response")) {
                            JsonNode responseNode = errorNode.get("response");
                            if (responseNode.has("message")) {
                                cleanErrorMessage = responseNode.get("message").asText();
                            }
                            if (responseNode.has("code")) {
                                extractedErrorCode = responseNode.get("code").asInt();
                            }
                        } else if (errorNode.has("message")) {
                            cleanErrorMessage = errorNode.get("message").asText();
                        }
                    } catch (Exception parseException) {
                        cleanErrorMessage = rawErrorJson;
                    }

                    int httpStatusValue = (e.status() > 0) ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();

                    // Map the integer code to the correct Enum instance safely
                    CustomStatus status = CustomStatus.fromCode(extractedErrorCode);

                    // Pass the clean extracted message to CustomException
                    throw new CustomException(cleanErrorMessage, status, httpStatusValue);
                }
                responseList.add(new EmployeeAttendanceResponse(
                        empId,
                        null,
                        null,
                        null,
                        null,
                        attendanceStatus,
                        null,
                        getWorkingDetailsOfEmployee(empId)
                ));
            }
        }

// responseList now contains the full timeline of events, followed by absent employees!

// responseList now contains sorted present employees first, and absent employees last!

        return new SingleResponse<>(
                responseList,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<List<EmployeeAttendanceHistoryResponse>> getDateWiseAttendanceRecords(DateWiseAttendanceRequest request) {

        LocalDate fromDate = request.getFromDate();
        LocalDate toDate = request.getToDate();
        Long employeeId = request.getEmployeeId();;

        // 1. Basic validation check
        if (toDate.isBefore(fromDate)) {
            throw new CustomException(null, CustomStatus.INVALID_DATE_RANGE, 409);
        }

        // 2. Fetch records from the database
        List<Attendance> attendanceRecords = attendanceRepository.findAttendanceByEmployeeAndDateRange(employeeId, fromDate, toDate);

        if (attendanceRecords.isEmpty()) {
            throw new CustomException(
                    "No attendance records found for this period",
                    CustomStatus.ATTENDANCE_RECORDS_NOT_FOUND,
                    404
            );
        }

        Set<LocalDate> processedDates = new HashSet<>();
        SingleResponse<Set<LocalDate>> apiResponse = leaveClient.getEmployeeLeaveDatesInRange(employeeId, fromDate, toDate);
        Set<LocalDate> leaveDates = apiResponse != null && apiResponse.getData() != null ?apiResponse.getData() : Collections.emptySet();

        // 3. Map the entities to your response DTOs using Java Streams
        List<EmployeeAttendanceHistoryResponse> historyResponse = attendanceRecords.stream()
                .map(record -> {
                    EmployeeAttendanceHistoryResponse dto = new EmployeeAttendanceHistoryResponse();
                    log.info("Record ID = {}", record.getId());
                    log.info("Record Attendance Type ID = {}", record.getAttendanceTypeId());
                    dto.setId(record.getId());
                    dto.setEmployeeId(record.getEmployeeId());

                    // Formatter guards to prevent null pointers if times are unrecorded
                    dto.setCheckInTime(record.getCheckInTime() != null ? record.getCheckInTime() : null);
                    dto.setCheckOutTime(record.getCheckOutTime() != null ? record.getCheckOutTime() : null);

                    // Map Enum to String
                    if (record.getAttendanceStatus() != null) {
                        dto.setAttendanceStatus(record.getAttendanceStatus().name());
                    }

                    dto.setTotalWorkMin(record.getTotalWorkMin());
                    dto.setAttendanceTypeId(record.getAttendanceTypeId());
                    log.info("DTO ID = {}", dto.getId());
                    log.info("DTO Attendance Type ID = {}", dto.getAttendanceTypeId());
                    return dto;
                })
                .collect(Collectors.toList());

        int missingLeaveCount = 0;
        for (LocalDate leaveDate : leaveDates) {
            if (!processedDates.contains(leaveDate)) {
                EmployeeAttendanceHistoryResponse leaveResponse = new EmployeeAttendanceHistoryResponse();
                leaveResponse.setId(null);
                leaveResponse.setEmployeeId(employeeId);
                leaveResponse.setTotalWorkMin(0L);
                leaveResponse.setAttendanceTypeId(null);
                leaveResponse.setCheckInTime(leaveDate.atStartOfDay());
                leaveResponse.setCheckOutTime(leaveDate.atStartOfDay());
                leaveResponse.setAttendanceStatus(AttendanceStatusEnum.ON_LEAVE.toString());

                historyResponse.add(leaveResponse);
                missingLeaveCount++;
            }
        }
        if (missingLeaveCount > 0) {
            log.info("Dynamically generated {} missing leave placeholder log(s) for employeeId: {}", missingLeaveCount, employeeId);
        }

        return new SingleResponse<>(
                historyResponse,
               CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<WeeklyAttendanceLogsOfEmployeeRes> getWeeklyAttendanceLogs(Long employeeId){
        LocalDate today = LocalDate.now();
        LocalDate mondayDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        log.info("Processing weekly attendance logs request. EmployeeId: {}, Target Week Start (Monday): {}", employeeId, mondayDate);

        LocalDateTime startOfWeek = LocalDateTime.of(mondayDate, LocalTime.MIDNIGHT);

        List<Attendance> weeklyLogs = attendanceRepository.findByEmployeeIdAndCheckInTimeAfterOrderByCheckInTimeDesc(employeeId, startOfWeek);
        log.info("Fetched {} database attendance record(s) for employeeId: {} since {}", weeklyLogs.size(), employeeId, startOfWeek);

        SingleResponse<Set<LocalDate>> apiResponse = leaveClient.getEmployeeLeaveDatesInRange(employeeId, mondayDate, LocalDate.now());
        Set<LocalDate> leaveDates = apiResponse != null && apiResponse.getData() != null ?apiResponse.getData() : Collections.emptySet();
        log.info("Fetched {} active leave date(s) from Leave Microservice for employeeId: {}. Leave Dates: {}", leaveDates.size(), employeeId, leaveDates);

        Set<LocalDate> processedDates = new HashSet<>();

        List<AttendanceResponse> logResponse = weeklyLogs.stream()
                .map(attendance -> {
                    AttendanceResponse response = modelMapper.map(attendance, AttendanceResponse.class);
                    LocalDate logDate = attendance.getCheckInTime().toLocalDate();
                    boolean isEmployeeOnLeave = leaveDates.contains(logDate);
//                    boolean isEmployeeOnLeave = leaveClient.isEmployeeOnLeave(employeeId, LocalDate.now());
                    if (isEmployeeOnLeave) {
                        log.debug("Overriding DB attendance log to ON_LEAVE for employeeId: {} on date: {}", employeeId, logDate);
                        response.setWorkMin(0L);
                        response.setAttendanceTypeId(null);
                        response.setCheckInTime(null);
                        response.setCheckOutTime(null);
                        response.setStatus(AttendanceStatusEnum.ON_LEAVE.toString());
                    } else {
                        response.setStatus(attendance.getAttendanceStatus().toString());
                    }
//                    else if (AttendanceStatusEnum.OFFLINE.equals(attendance.getAttendanceStatus()) && ) {
//                        response.setStatus("PRESENT");
                    return response;
                })
                .collect(Collectors.toList());

        int missingLeaveCount = 0;
        for (LocalDate leaveDate : leaveDates) {
            if (!processedDates.contains(leaveDate)) {
                AttendanceResponse leaveResponse = new AttendanceResponse();
                leaveResponse.setWorkMin(0L);
                leaveResponse.setAttendanceTypeId(null);
                leaveResponse.setCheckInTime(leaveDate.atStartOfDay());
                leaveResponse.setCheckOutTime(leaveDate.atStartOfDay());
                leaveResponse.setStatus(AttendanceStatusEnum.ON_LEAVE.toString());

                // Assuming your response model holds the date context, or you can sort/insert predictably
                logResponse.add(leaveResponse);
                missingLeaveCount++;
            }
        }
        if (missingLeaveCount > 0) {
            log.info("Dynamically generated {} missing leave placeholder log(s) for employeeId: {}", missingLeaveCount, employeeId);
        }

        Long totalWorkedMinutes = weeklyLogs.stream()
                .filter(log -> log.getCheckInTime() != null && log.getCheckOutTime() != null)
                .mapToLong(log -> Duration.between(log.getCheckInTime(), log.getCheckOutTime()).toMinutes())
                .sum();

        log.info("Calculation complete. Total weekly minutes worked for employeeId: {} is {} mins across {} output logs",
                employeeId, totalWorkedMinutes, logResponse.size());

        WeeklyAttendanceLogsOfEmployeeRes response = new WeeklyAttendanceLogsOfEmployeeRes(totalWorkedMinutes, logResponse);

        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public ApiResponse<?> updateCheckoutRecordsByEmpId(UpdateCheckOutRecordsRequest request) {

        Attendance attendance = attendanceRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.ATTENDANCE_RECORDS_NOT_FOUND, 404));

        if(!request.getEmployeeId().equals(attendance.getEmployeeId())){
            throw new CustomException(null, CustomStatus.EMPLOYEE_ID_NOT_FOUND, 404);
        }

//        LocalDate checkedInDate = attendance.getCheckInTime().toLocalDate();
//        LocalDate requestedCheckedOutDate = request.getCheckoutDateTime().toLocalDate();

        attendance.setAdminRemarks(request.getRemarks());
        attendance.setCheckOutTime(request.getCheckoutDateTime().withNano(0));
        attendance.setAdminModified(true);
        attendanceRepository.save(attendance);
        return new ApiResponse<>(
                true,
                "Checkout time updated",
                null,
                LocalDateTime.now(),
                200
        );
    }

    // HELPER method
    private boolean isEmployeeOnLeaveInMicroservice(Long employeeId, LocalDate date) {
        return leaveClient.isEmployeeOnLeave(employeeId, date);
    }

    //Helper
    public Long getWorkingDetailsOfEmployee(Long employeeId){
        LocalDateTime fromDate = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .with(LocalTime.MIN);
        LocalDateTime toDate = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
                .with(LocalTime.MAX);

        Long totalWorkMin = attendanceRepository.getTotalWorkMin(employeeId, fromDate, toDate)
                .orElse(0L);

        return totalWorkMin;
    }
}
