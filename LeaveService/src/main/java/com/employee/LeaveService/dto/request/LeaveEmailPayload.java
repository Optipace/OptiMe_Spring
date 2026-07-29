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
public class LeaveEmailPayload {
    private String approverName;
    private String applicantEmployeeId;
    private String applicantName;
    private String approverEmailId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
    private String leaveType;
}
