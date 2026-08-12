package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.request.ApproveLeaveRequest;
import com.employee.LeaveService.dto.request.RejectLeaveRequest;
import com.employee.LeaveService.dto.request.UpdateLeaveRequest;
import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.LeaveTypeResponse;
import com.employee.LeaveService.dto.response.SingleResponse;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface LeaveInternalService {
    ApiResponse<?> getAllAppliedLeaves();

    ApiResponse<List<LeaveTypeResponse>> getLeaveTypeList();

    boolean isEmployeeOnLeave(String employeeId, LocalDate today);

//    ApiResponse<?> updateLeave(UpdateLeaveRequest request, String approvedEmployeeId);

    ApiResponse<?> approveLeave(ApproveLeaveRequest request, String authorityEmployeeId);

    ApiResponse<?> rejectLeave(RejectLeaveRequest request, String authorityEmployeeId);

    ApiResponse<Set<LocalDate>> getEmployeeLeaveDatesInRange(String employeeId, LocalDate startDate, LocalDate endDate);
}
