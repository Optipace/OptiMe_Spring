package com.employee.AdminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyAttendanceLogsOfEmployeeRes {
    private Long totalWorkMins;
    private List<AttendanceResponse> attendanceLogs;
}
