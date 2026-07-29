package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.request.LeaveRequest;
import com.employee.LeaveService.dto.request.UpdateLeaveRequest;
import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.SingleResponse;
import org.springframework.web.bind.annotation.RequestBody;

public interface LeaveService {
    public SingleResponse<?> saveLeaveApplication(LeaveRequest request, String applicantEmployeeId, String applicantEmployeeName, String applicantEmailId);

    public SingleResponse<?> updateLeave(UpdateLeaveRequest request, String approvedEmployeeId);
}
