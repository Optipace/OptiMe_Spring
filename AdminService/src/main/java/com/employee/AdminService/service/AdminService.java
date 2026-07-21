package com.employee.AdminService.service;

import com.employee.AdminService.dto.request.*;
import com.employee.AdminService.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    public SingleResponse<?> addNewUser(RegisterRequest request, String adminEmployeeId);

    public SingleResponse<PageResponse<EmployeeResponse>> getAllEmployee(Pageable pageable);

    public SingleResponse<?> addNewOffice(AddNewOfficeRequest request);

    public SingleResponse<PageResponse<OfficeResponse>> getOfficeList(Pageable pageable);

    public SingleResponse<?> updateOffice(OfficeRequest request);

    public SingleResponse<PageResponse<String>> getOfficeNames(Pageable pageable);

    public SingleResponse<List<FeedbackResponse>> getFeedback();

    public SingleResponse<?> updateFeedback(FeedbackUpdateRequest request);

    public SingleResponse<?> getAllAppliedLeaves();

    public SingleResponse<?> sendBroadcastMessage(NotificationRequest request);

    public SingleResponse<MasterResponse> getMasterDetails();
}
