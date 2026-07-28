package com.employee.AdminService.dto.request;

import com.employee.AdminService.enums.LeaveStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLeaveRequest {
    private Long leaveId;
    private LeaveStatusEnum leaveStatus;
}
