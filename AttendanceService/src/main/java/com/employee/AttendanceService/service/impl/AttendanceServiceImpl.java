package com.employee.AttendanceService.service.impl;

import com.employee.AttendanceService.client.EmployeeClient;
import com.employee.AttendanceService.client.LeaveClient;
import com.employee.AttendanceService.config.AppProperties;
import com.employee.AttendanceService.dto.request.UpdateEmployeeStatusPayload;
import com.employee.AttendanceService.dto.response.*;
import com.employee.AttendanceService.enums.AttendanceStatusEnum;
import com.employee.AttendanceService.enums.CustomStatus;
import com.employee.AttendanceService.enums.EmployeeAccountStatus;
import com.employee.AttendanceService.exception.CustomException;
import com.employee.AttendanceService.model.*;
import com.employee.AttendanceService.repository.AttendanceRepository;
import com.employee.AttendanceService.service.AttendanceService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final ModelMapper mapperModel;

    private final AttendanceRepository attendanceRepository;

    private final EmployeeClient employeeClient;

    private final ObjectMapper objectMapper;

    private static final long MAX_IMAGE_SIZE = 1024 * 1024;

    private final AppProperties appProperties;

    private final LeaveClient leaveClient;

    @Override
    @Transactional
    public SingleResponse<?> employeeCheckIn(String employeeId, MultipartFile file,
                                          String latitude, String longitude, Long attendanceTypeId) {

        // TODO: Need to check today check in and need to be checked out the previous day check in history by the scheduler
//        boolean isAlreadyCheckedIn = attendanceRepository.existsByEmployeeIdAndCheckOutTimeIsNull(employeeId);
        LocalDateTime checkInTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
        boolean isAlreadyCheckedIn = attendanceRepository.existsByEmployeeIdAndCheckOutTimeIsNullAndCheckInTimeAfter(employeeId, checkInTime);

        if(isAlreadyCheckedIn)
            throw new CustomException(null, CustomStatus.ALREADY_CHECKED_IN, 201);

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setCheckOutTime(null);
        attendance.setTotalWorkMin(0L);
        attendance.setAttendanceStatus(AttendanceStatusEnum.ONLINE);
        attendance.setLatitude(latitude);
        attendance.setLongitude(longitude);
        boolean workTypeIdExists = employeeClient.checkWorkTypeIdExists(attendanceTypeId);
        log.info("Work type exists :{}",workTypeIdExists);
        if(!workTypeIdExists)
            throw new CustomException("Work Type not found", HttpStatus.NOT_FOUND);
        log.info("{} it exists saving attendance type id as {}",workTypeIdExists,attendanceTypeId);
        attendance.setAttendanceTypeId(attendanceTypeId);

        if (file != null && !file.isEmpty()){
//            throw new CustomException("File is empty", HttpStatus.BAD_REQUEST);
            log.info("Incoming file size {}", file.getSize());
            if (file.getSize() > MAX_IMAGE_SIZE)
                throw new CustomException("Image exceeds 1MB limit", HttpStatus.BAD_REQUEST);

            String contentType = file.getContentType();

            log.info("Incoming content type {}", contentType);

            if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType) || "image/jpg".equals(contentType))) {
                throw new CustomException("Only JPEG or PNG files are allowed", HttpStatus.BAD_REQUEST);
            }

            String filePath = saveFile(file, appProperties.getImage().getUploadDir() + "EverydayAttendanceSelfies/" + employeeId + "/");
            log.info("Image saved path {}", filePath);
            attendance.setFilePath(filePath);
        }else{
            log.info("Employee {} checked in without a photo", employeeId);
            attendance.setFilePath("NULL");
        }

        attendance = attendanceRepository.save(attendance);
        boolean attendanceCreated = true;

//        try{
//            UpdateEmployeeStatusPayload payload = new UpdateEmployeeStatusPayload();
//            payload.setEmployeeId(attendance.getEmployeeId());
//            if(apiResponse != null && apiResponse.getData() != null && apiResponse.getData().equalsIgnoreCase("WFO")){
//                payload.setEmployeeAccountStatus(EmployeeAccountStatus.IN_OFFICE);
//            }else {
//                payload.setEmployeeAccountStatus(EmployeeAccountStatus.ONLINE);
//            }
//            employeeClient.updateEmployeeStatus(payload);
//            log.info("Employee service called successfully after check-in");
//        }catch (FeignException fe){
//            // THE SAFE COMPENSATING TRANSACTION
//            if (attendanceCreated) {
//                try {
//                    log.warn("Feign call failed. Manually rolling back attendance for: {}", employeeId);
//                    attendanceRepository.delete(attendance);
//                } catch (Exception rollbackEx) {
//                    log.error("CRITICAL ALARM: Database rollback failed for {}. Manual cleanup required! Error: {}",
//                            employeeId, rollbackEx.getMessage());
//                }
//            }
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
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    @Transactional
    public SingleResponse<?> employeeCheckOut(String employeeId){
//        Attendance attendance = attendanceRepository.findByEmployeeIdAndCheckOutTimeIsNull(employeeId)
//                .orElseThrow(() -> new CustomException("No active check-in record found for this employee", HttpStatus.NOT_FOUND));

        LocalDateTime checkInTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
        Attendance attendance = attendanceRepository.findByEmployeeIdAndCheckOutTimeIsNullAndCheckInTimeAfter(employeeId, checkInTime)
                        .orElseThrow(() -> new CustomException(null, CustomStatus.CHECK_IN_RECORD_NOT_FOUND, 201));

        attendance.setTotalWorkMin(Duration.between(attendance.getCheckInTime(), LocalDateTime.now()).toMinutes());

        if(attendance.getCheckOutTime() == null)
            attendance.setCheckOutTime(LocalDateTime.now());

        attendance.setAttendanceStatus(AttendanceStatusEnum.OFFLINE);
        attendanceRepository.save(attendance);
        try{
            UpdateEmployeeStatusPayload payload = new UpdateEmployeeStatusPayload();
            payload.setEmployeeId(employeeId);
            payload.setEmployeeAccountStatus(EmployeeAccountStatus.ACTIVE);
            employeeClient.updateEmployeeStatus(payload);
            log.info("Employee service called after check-out");
        }catch (FeignException fe){
            String rawErrorJson = fe.contentUTF8();
            String cleanErrorMessage = "Micro-Services failed";
            try{
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if(errorNode.has("message")){
                    cleanErrorMessage = errorNode.get("message").toString();
                }else{
                    cleanErrorMessage = rawErrorJson;
                }
            }catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }
            // Resolve status code safely.
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
                log.error("Employee profile service unavailable");
            }
            throw new CustomException(cleanErrorMessage, responseStatus);
        }
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
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
    public SingleResponse<WorkingDetailsResponse> getWorkingDetails(String employeeId){
        LocalDateTime fromDate = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .with(LocalTime.MIN);
        LocalDateTime toDate = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
                .with(LocalTime.MAX);

        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

        List<Attendance> attendanceList = attendanceRepository.findTodayAttendanceByEmployeeId(employeeId,startOfDay,endOfDay)
                .orElseThrow(() -> new CustomException(null, CustomStatus.ATTENDANCE_RECORDS_NOT_FOUND, 201));

        Long totalWorkMin = attendanceRepository.getTotalWorkMin(employeeId, fromDate, toDate)
                .orElse(0L);

        List<AttendanceResponse> logResponse = attendanceList.stream()
                .map(attendance -> mapperModel.map(attendance, AttendanceResponse.class))
                .toList();

        WorkingDetailsResponse response = new WorkingDetailsResponse(totalWorkMin, logResponse);

        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }

    public SingleResponse<WeeklyAttendanceLogsOfEmployeeRes> getWeeklyAttendanceLogs(String employeeId){
        LocalDate mondayDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        log.info("Extracting present week Monday date {}",mondayDate);

        LocalDateTime startOfWeek = LocalDateTime.of(mondayDate, LocalTime.MIDNIGHT);

        List<Attendance> weeklyLogs = attendanceRepository.findByEmployeeIdAndCheckInTimeAfterOrderByCheckInTimeAsc(employeeId, startOfWeek);

        List<AttendanceResponse> logResponse = weeklyLogs.stream()
                .map(attendance -> mapperModel.map(attendance, AttendanceResponse.class))
                .toList();

        Long totalWorkedMinutes = weeklyLogs.stream()
                .filter(log -> log.getCheckInTime() != null && log.getCheckOutTime() != null)
                .mapToLong(log -> Duration.between(log.getCheckInTime(), log.getCheckOutTime()).toMinutes())
                .sum();

        WeeklyAttendanceLogsOfEmployeeRes response = new WeeklyAttendanceLogsOfEmployeeRes(totalWorkedMinutes, logResponse);

        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> getTodayAttendanceRecords() {
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
                String attendanceStatus = "ABSENT"; // Default status

                // Network call for each absent employee
                try {
                    boolean isOnLeave = leaveClient.isEmployeeOnLeave(empId, today);// TODO: can make bulk api call to load leave of emp all at once
                    if (isOnLeave) {
                        attendanceStatus = "LEAVE";
                    }
                } catch (FeignException fe) {
                    log.error("Leave service call failed for employee: {}", empId, fe);
                    // Defaulting to ABSENT if the service fails or throws exception
                }
                responseList.add(new EmployeeAttendanceResponse(
                        empId,
                        null,
                        null,
                        null,
                        null,
                        attendanceStatus,
                        null
                ));
            }
        }

// responseList now contains the full timeline of events, followed by absent employees!

        return new SingleResponse<>(responseList, CustomStatus.SUCCESS);
    }

    private String saveFile(MultipartFile file, String folder) {
        try {

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(folder + fileName);

            Files.createDirectories(path.getParent());

            // Use streaming (better for large files)
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return path.toString(); // return file path

        } catch (IOException e) {
            throw new CustomException(null, CustomStatus.FILE_UPLOAD_FAILED, 201);
        }
    }
}
