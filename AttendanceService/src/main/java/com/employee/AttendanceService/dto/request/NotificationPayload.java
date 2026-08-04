package com.employee.AttendanceService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationPayload {
    private String employeeId;
    private String title;
    private String message;
    private String type;
}
