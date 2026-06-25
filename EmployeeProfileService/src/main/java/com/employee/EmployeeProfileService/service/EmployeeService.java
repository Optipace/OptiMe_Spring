package com.employee.EmployeeProfileService.service;

import com.employee.EmployeeProfileService.dto.request.FeedbackRequest;
import com.employee.EmployeeProfileService.dto.request.FeedbackUpdateRequest;
import com.employee.EmployeeProfileService.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {
    public ApiResponse<List<EmployeeResponse>> getAllEmployees();

    public ApiResponse<EmployeeResponse> getEmployeeDetails(String employeeId);

    public ApiResponse<EmployeeResponse> getEmployeeByEmployeeId(String employeeId);

    public ApiResponse<?> getOfficeNames();

    public ApiResponse<?> uploadEmployeeProfile(MultipartFile file, String employeeId);

    public ApiResponse<?> saveFeedback(FeedbackRequest request, String authHeader);

    public ApiResponse<List<FeedbackResponse>> getFeedback();

    public ApiResponse<?> updateFeedback(FeedbackUpdateRequest request);

//    public ResponseEntity<Resource> getEmployeeProfile(String authHeader);
}
