package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.LeaveTypeResponse;

import java.util.List;

public interface LeaveInternalService {
    ApiResponse<?> getAllAppliedLeaves();

    ApiResponse<List<LeaveTypeResponse>> getLeaveTypeList();
}
