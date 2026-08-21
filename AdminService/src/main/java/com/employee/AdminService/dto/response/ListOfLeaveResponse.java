package com.employee.AdminService.dto.response;

import com.employee.AdminService.enums.LeaveStatusEnum;
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
public class ListOfLeaveResponse {
    private Long leaveId;
    private String employeeName;
    private Long employeeId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private LocalDateTime appliedOn;
    private String leaveReason;
    private String remarks;
    private Long leaveTypeId;
    private Integer numberOfLeavesApplied;
    private String approvedBy;
    private Long approvedByEmployeeId;
    private String approverName;
    private Long approverEmployeeId;
    private LeaveStatusEnum leaveStatus;
    private Integer remainingLeaves;
}
