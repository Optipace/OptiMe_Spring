package com.employee.AttendanceService.dto.response;

import com.employee.AttendanceService.enums.AttendanceStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceStatusResponse {
    private String attendanceStatus;
}
