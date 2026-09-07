package com.employee.AttendanceService.dto.response;

import com.employee.AttendanceService.enums.AttendanceStatusEnum;
import com.employee.AttendanceService.enums.OneMonthAttendanceStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OneMonthAttendanceResponse {

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private Long totalWorkMin;

    private OneMonthAttendanceStatusEnum attendanceStatus;

}
