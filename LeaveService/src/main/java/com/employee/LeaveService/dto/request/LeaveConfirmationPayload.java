package com.employee.LeaveService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveConfirmationPayload {
    private String applicantEmailId;
    private String applicantName;
    private String leaveType;
    private LocalDate fromDate;
    private LocalDate toDate;
}
