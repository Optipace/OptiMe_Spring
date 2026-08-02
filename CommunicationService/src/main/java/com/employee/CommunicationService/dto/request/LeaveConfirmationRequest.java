package com.employee.CommunicationService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveConfirmationRequest {
    private String applicantEmailId;
    private String applicantName;
    private String leaveType;
    private LocalDate fromDate;
    private LocalDate toDate;
}
