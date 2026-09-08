package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.request.PutWorkingSatPayload;
import com.employee.LeaveService.dto.request.WorkingSatPayload;
import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.dto.response.WorkingSatResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface WorkingSatService {

    SingleResponse<?> saveWorkingSaturday(WorkingSatPayload payload);

    SingleResponse<List<WorkingSatResponse>> getWorkingSaturdayByOfficeId(Long officeId, Integer month, Integer year);

    SingleResponse<WorkingSatResponse> updateWorkingSaturday(Long id, @Valid PutWorkingSatPayload payload);

    SingleResponse<?> deleteWorkingSaturday(Long id);

}
