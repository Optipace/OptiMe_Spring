package com.employee.AttendanceService.dto.request;

import com.employee.AttendanceService.enums.WorkTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckInRequest {
    private String latitude;
    private String longitude;
    private WorkTypeEnum attendanceType;
}
