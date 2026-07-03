package com.employee.LeaveService.dto.response;

import com.employee.LeaveService.enums.LeaveStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveResponse {
    private String employeeId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private LocalDateTime appliedOn;
    private String reason;
    private String approvedBy;
    private LeaveStatusEnum leaveStatus;
}
