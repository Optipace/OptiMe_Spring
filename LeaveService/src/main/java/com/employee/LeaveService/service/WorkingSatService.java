package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.request.WorkingSatPayload;
import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.dto.response.WorkingSatResponse;
import org.springframework.stereotype.Service;

import java.util.List;

public interface WorkingSatService {

    SingleResponse<?> saveWorkingSaturday(WorkingSatPayload payload);

    SingleResponse<List<WorkingSatResponse>> getWorkingSaturdayByOfficeId(Long officeId);
}
