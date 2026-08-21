package com.employee.LeaveService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationPayload {
    private Long employeeId;
    private String title;
    private String message;
    private String type;
}
