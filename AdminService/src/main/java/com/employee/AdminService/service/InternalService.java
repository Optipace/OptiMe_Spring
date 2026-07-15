package com.employee.AdminService.service;

import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.OfficeResponse;

import java.util.List;

public interface InternalService {
    ApiResponse<List<OfficeResponse>> getOfficeList();

    ApiResponse<OfficeResponse> getOfficeDetailsByOfficeId(String officeId);

    ApiResponse<?> getOfficeNames();
}
