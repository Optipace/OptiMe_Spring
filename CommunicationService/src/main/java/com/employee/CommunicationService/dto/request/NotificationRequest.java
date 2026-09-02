package com.employee.CommunicationService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    private Long employeeId; // Who receives this?
    private String title; // ex: "New employee created"
    private String message; // ex: "OPTI001 has successfully registered
    private String type; // ex: "INFO" , "ALERT" , "SUCCESS"
    private String timestamp = LocalDateTime.now().toString();
}
