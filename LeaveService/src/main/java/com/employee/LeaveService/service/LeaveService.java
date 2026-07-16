package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.request.LeaveRequest;
import com.employee.LeaveService.dto.request.UpdateLeaveRequest;
import com.employee.LeaveService.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;

public interface LeaveService {
    public ApiResponse<?> saveLeaveApplication(LeaveRequest request, String applicantEmployeeId, String applicantEmployeeName);

    ApiResponse<?> updateLeave(UpdateLeaveRequest request, String approvedEmployeeId);
}
