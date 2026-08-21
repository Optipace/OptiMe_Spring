package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.request.ApproveLeaveRequest;
import com.employee.LeaveService.dto.request.CancelMyLeaveRequest;
import com.employee.LeaveService.dto.request.LeaveRequest;
import com.employee.LeaveService.dto.request.RejectLeaveRequest;
import com.employee.LeaveService.dto.response.SingleResponse;
import org.springframework.web.bind.annotation.RequestHeader;

public interface LeaveService {
    SingleResponse<?> saveLeaveApplication(LeaveRequest request, String employeeId, String applicantEmployeeId, String applicantEmployeeName, String applicantEmailId);

    SingleResponse<?> approveLeave(ApproveLeaveRequest request, String authorityId);

    SingleResponse<?> rejectLeave(RejectLeaveRequest request, String authorityEmployeeId);

    SingleResponse<?> getMyAppliedLeaves(String employeeId);

    SingleResponse<?> cancelMyLeave(CancelMyLeaveRequest request, String employeeId);

    SingleResponse<?> getAppliedLeavesForMe(String employeeId);

    SingleResponse<?> getAllPendingLeaves();

    SingleResponse<?> getAllProcessedLeaves();
}
