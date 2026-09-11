package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.request.ApproveLeaveRequest;
import com.employee.LeaveService.dto.request.AvailableLeavesPayload;
import com.employee.LeaveService.dto.request.RejectLeaveRequest;
import com.employee.LeaveService.dto.request.UpdateLeaveRequest;
import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.LeaveTypeResponse;
import com.employee.LeaveService.dto.response.ListOfLeaveResponse;
import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.model.AvailableLeaves;
import jakarta.validation.Valid;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface LeaveInternalService {
    SingleResponse<List<ListOfLeaveResponse>> getAllAppliedLeaves();

    SingleResponse<List<LeaveTypeResponse>> getLeaveTypeList();

    boolean isEmployeeOnLeave(Long employeeId, LocalDate today);

//    ApiResponse<?> updateLeave(UpdateLeaveRequest request, String approvedEmployeeId);

    SingleResponse<?> approveLeave(ApproveLeaveRequest request, String authorityEmployeeId);

    SingleResponse<?> rejectLeave(RejectLeaveRequest request, String authorityEmployeeId);

    SingleResponse<Set<LocalDate>> getEmployeeLeaveDatesInRange(Long employeeId, LocalDate startDate, LocalDate endDate);

    SingleResponse<AvailableLeaves> saveAvailableLeaves(@Valid AvailableLeavesPayload payload);

    SingleResponse<?> deleteAvailableLeaves(Long id);

    SingleResponse<AvailableLeaves> getAvailableLeavesById(Long id);
}
