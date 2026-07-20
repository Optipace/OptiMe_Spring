package com.employee.EmployeeProfileService.service;

import com.employee.EmployeeProfileService.dto.request.CompleteProfileRequest;
import com.employee.EmployeeProfileService.dto.request.EmployeeProfileRequest;
import com.employee.EmployeeProfileService.dto.request.FeedbackUpdateRequest;
import com.employee.EmployeeProfileService.dto.request.UpdateEmployeeStatusRequest;
import com.employee.EmployeeProfileService.dto.response.ApiResponse;
import com.employee.EmployeeProfileService.dto.response.EmployeeInternalResponse;
import com.employee.EmployeeProfileService.dto.response.EmployeeResponse;
import com.employee.EmployeeProfileService.dto.response.FeedbackResponse;

import java.util.List;

public interface EmployeeInternalService {

    public ApiResponse<?> createProfile(EmployeeProfileRequest request);

    public ApiResponse<?> completeProfile(CompleteProfileRequest request);

    public ApiResponse<EmployeeResponse> getProfile(String employeeId);

    public ApiResponse<?> getMasterDetails();

    public boolean checkEmployeeByEmployeeId(String employeeId);

    public ApiResponse<?> updateEmployeeStatus(UpdateEmployeeStatusRequest request);

    public ApiResponse<EmployeeInternalResponse> getEmployeeByEmployeeId(String employeeId);

    public ApiResponse<?> deleteIdentity(String employeeId);

    public ApiResponse<List<FeedbackResponse>> getFeedback();

    public ApiResponse<?> updateFeedback(FeedbackUpdateRequest request);

    public boolean getWorkTypeId(Long workTypeId);

}
