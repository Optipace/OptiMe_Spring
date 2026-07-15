package com.employee.AdminService.service;

import com.employee.AdminService.dto.request.NotificationRequest;
import com.employee.AdminService.dto.request.OfficeRequest;
import com.employee.AdminService.dto.request.RegisterRequest;
import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.OfficeResponse;
import com.employee.AdminService.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    public ApiResponse<?> addNewUser(RegisterRequest request, String adminEmployeeId);

    public ApiResponse<PageResponse<OfficeResponse>> getOfficeList(Pageable pageable);

    public ApiResponse<?> updateOffice(OfficeRequest request);

    public ApiResponse<PageResponse<String>> getOfficeNames(Pageable pageable);

    public ApiResponse<?> getAllAppliedLeaves();

    public ApiResponse<?> sendBroadcastMessage(NotificationRequest request);
}
