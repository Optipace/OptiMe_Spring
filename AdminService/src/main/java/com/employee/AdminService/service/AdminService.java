package com.employee.AdminService.service;

import com.employee.AdminService.dto.request.*;
import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.FeedbackResponse;
import com.employee.AdminService.dto.response.OfficeResponse;
import com.employee.AdminService.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    public ApiResponse<?> addNewUser(RegisterRequest request, String adminEmployeeId);

    public ApiResponse<?> addNewOffice(AddNewOfficeRequest request);

    public ApiResponse<PageResponse<OfficeResponse>> getOfficeList(Pageable pageable);

    public ApiResponse<?> updateOffice(OfficeRequest request);

    public ApiResponse<PageResponse<String>> getOfficeNames(Pageable pageable);

    public ApiResponse<List<FeedbackResponse>> getFeedback();

    public ApiResponse<?> updateFeedback(FeedbackUpdateRequest request);

    public ApiResponse<?> getAllAppliedLeaves();

    public ApiResponse<?> sendBroadcastMessage(NotificationRequest request);
}
