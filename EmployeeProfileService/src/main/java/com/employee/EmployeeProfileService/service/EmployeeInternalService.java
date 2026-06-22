package com.employee.EmployeeProfileService.service;

import com.employee.EmployeeProfileService.dto.request.CompleteProfileRequest;
import com.employee.EmployeeProfileService.dto.request.EmployeeProfileRequest;
import com.employee.EmployeeProfileService.dto.response.ApiResponse;

public interface EmployeeInternalService {

    public ApiResponse<?> createProfile(EmployeeProfileRequest request);

    public ApiResponse<?> completeProfile(CompleteProfileRequest request);

}
