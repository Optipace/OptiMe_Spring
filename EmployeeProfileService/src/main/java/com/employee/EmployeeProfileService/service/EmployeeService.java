package com.employee.EmployeeProfileService.service;

import com.employee.EmployeeProfileService.dto.request.FeedbackRequest;
import com.employee.EmployeeProfileService.dto.request.FeedbackUpdateRequest;
import com.employee.EmployeeProfileService.dto.request.UpdateEmployeeRequest;
import com.employee.EmployeeProfileService.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {
    public SingleResponse<List<ListOfEmployeeResponse>> getAllEmployees();

    public SingleResponse<EmployeeResponse> getEmployeeDetails(String employeeId);

    public SingleResponse<EmployeeResponse> getEmployeeByEmployeeId(String employeeId);

    public SingleResponse<?> getOfficeNames();

    public SingleResponse<?> uploadEmployeeProfile(MultipartFile file, String employeeId);

    public SingleResponse<?> saveFeedback(FeedbackRequest request, String authHeader);

    public SingleResponse<List<FeedbackResponse>> getFeedback();

    public SingleResponse<?> updateFeedback(FeedbackUpdateRequest request);

    public SingleResponse<List<ListOfAdminResponse>> getAllAdminDetails();

    public SingleResponse<?> uploadDocument(MultipartFile file, String employeeId, String documentType, String documentNo,Long userId);

    public SingleResponse<?> updateEmployee(UpdateEmployeeRequest request);

//    public ResponseEntity<Resource> getEmployeeProfile(String authHeader);
}
