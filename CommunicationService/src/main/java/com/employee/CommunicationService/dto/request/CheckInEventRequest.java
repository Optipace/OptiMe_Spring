package com.employee.CommunicationService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckInEventRequest {
    private Long employeeId;
    private String employeeName;
    private String checkInTime;
    private String location;
    private String workType;
}
