package com.employee.EmployeeProfileService.service;

import com.employee.EmployeeProfileService.dto.request.CompleteProfileRequest;
import com.employee.EmployeeProfileService.dto.request.EmployeeProfileRequest;
import com.employee.EmployeeProfileService.dto.request.UpdateEmployeeStatusRequest;
import com.employee.EmployeeProfileService.dto.response.ApiResponse;
import com.employee.EmployeeProfileService.dto.response.EmployeeInternalResponse;
import com.employee.EmployeeProfileService.dto.response.EmployeeResponse;
import org.springframework.web.bind.annotation.RequestParam;

public interface EmployeeInternalService {

    public ApiResponse<?> createProfile(EmployeeProfileRequest request);

    public ApiResponse<?> completeProfile(CompleteProfileRequest request);

    public ApiResponse<EmployeeResponse> getProfile(String employeeId);

    public ApiResponse<?> getMasterDetails();

    public boolean checkEmployeeByEmployeeId(String employeeId);

    public ApiResponse<?> updateEmployeeStatus(UpdateEmployeeStatusRequest request);

    public ApiResponse<EmployeeInternalResponse> getEmployeeByEmployeeId(String employeeId);

    public ApiResponse<?> deleteIdentity(String employeeId);

}
