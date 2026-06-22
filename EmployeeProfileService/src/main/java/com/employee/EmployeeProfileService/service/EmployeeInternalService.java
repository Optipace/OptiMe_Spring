package com.employee.EmployeeProfileService.service;

import com.employee.EmployeeProfileService.dto.request.CompleteProfileRequest;
import com.employee.EmployeeProfileService.dto.request.EmployeeProfileRequest;
import com.employee.EmployeeProfileService.dto.response.ApiResponse;
import com.employee.EmployeeProfileService.dto.response.EmployeeResponse;

public interface EmployeeInternalService {

    public ApiResponse<?> createProfile(EmployeeProfileRequest request);

    public ApiResponse<?> completeProfile(CompleteProfileRequest request);

    public ApiResponse<EmployeeResponse> getProfile(String employeeId);

}
