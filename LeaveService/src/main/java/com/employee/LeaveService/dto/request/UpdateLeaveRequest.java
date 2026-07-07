package com.employee.LeaveService.dto.request;

import com.employee.LeaveService.enums.LeaveStatusEnum;
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
