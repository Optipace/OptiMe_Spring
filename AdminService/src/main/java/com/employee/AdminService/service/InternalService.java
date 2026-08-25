package com.employee.AdminService.service;

import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.OfficeResponse;
import com.employee.AdminService.dto.response.SingleResponse;

import java.util.List;

public interface InternalService {
    SingleResponse<List<OfficeResponse>> getOfficeList();

    SingleResponse<OfficeResponse> getOfficeDetailsByOfficeId(Long officeId);

    SingleResponse<?> getOfficeNames();
}
