package com.employee.AttendanceService.service.impl;

import com.employee.AttendanceService.dto.request.*;
import com.employee.AttendanceService.dto.response.*;
import com.employee.AttendanceService.enums.EmployeeStatusEnum;
import com.employee.AttendanceService.exception.CustomException;
import com.employee.AttendanceService.model.*;
import com.employee.AttendanceService.repository.AttendanceRepository;
import com.employee.AttendanceService.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final ModelMapper mapperModel;

    private final AttendanceRepository attendanceRepository;

    @Override
    public ApiResponse<?> employeeLogin(EmployeeLoginRequest request) {
//        Employee employee = employeeRepository.findEmployeeByEmployeeId(request.getEmployeeId())
//                .orElseThrow(() -> new CustomException("Sorry! Employee not found", HttpStatus.NOT_FOUND));

//        Location location = locationRepository.findByLocationName(request.getLocation())
//                .orElseThrow(() -> new CustomException("Location not found", HttpStatus.NOT_FOUND));

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(request.getEmployeeId());
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setCheckOutTime(null);
        attendance.setTotalWorkMin(0L);
//        attendance.setCheckInLocation(location);
//        attendance.setCheckInCity(location.getCity());
//        attendance.setCheckInState(location.getCity().getState());
        attendance.setEmployeeStatus(EmployeeStatusEnum.ONLINE);

//        if(employee.getEmployeeStatus() == null){
//            employee.setEmployeeStatus(EmployeeStatusEnum.ACTIVE);
//            employeeRepository.save(employee);
//        }
//        employee.setEmployeeStatus(EmployeeStatusEnum.ACTIVE);
//        employeeRepository.save(employee);
        attendanceRepository.save(attendance);

        return new ApiResponse<>(
                true,
                "Login Successful",
                null,
                LocalDateTime.now(),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<?> employeeLogout(EmployeeLogoutRequest request){
        Attendance attendance = attendanceRepository.findEmployeeByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new CustomException("No attendance records found for today", HttpStatus.NOT_FOUND));

        attendance.setTotalWorkMin(Duration.between(attendance.getCheckInTime(), LocalDateTime.now()).toMinutes());

        if(attendance.getCheckOutTime() == null)
            attendance.setCheckOutTime(LocalDateTime.now());

        attendance.setEmployeeStatus(EmployeeStatusEnum.OFFLINE);
        attendanceRepository.save(attendance);
        return new ApiResponse<>(
                true,
                "Logout Successful",
                null,
                LocalDateTime.now(),
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
    public ApiResponse<List<AttendanceResponse>> getWorkingDetails(String employeeId){
        LocalDateTime fromDate = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                                    .with(LocalTime.MIN);
        LocalDateTime toDate = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
                                                    .with(LocalTime.MAX);

        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

//        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
//                .orElseThrow(() -> new CustomException("Employee records not found", HttpStatus.NOT_FOUND));

        List<Attendance> attendanceList = attendanceRepository.findTodayAttendanceByEmployeeId(1L/*employee.getId()*/,startOfDay,endOfDay)
                .orElseThrow(() -> new CustomException("No attendance records found", HttpStatus.NOT_FOUND));

        Long totalWorkMin = attendanceRepository.getTotalWorkMin(1L/*employee.getId()*/, fromDate, toDate)
                .orElse(0L);

        List<AttendanceResponse> response = attendanceList.stream()
                .map(attendance -> {
                    AttendanceResponse res = mapperModel.map(attendance, AttendanceResponse.class);
                    res.setWorkMin(totalWorkMin);
                    return res;
                })
                .toList();

        return new ApiResponse<>(
                true,
                "Total working details",
                response,
                LocalDateTime.now(),
                HttpStatus.OK
        );
    }
}
