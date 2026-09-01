package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.response.SingleResponse;

import java.time.LocalDate;

public interface WorkingSatInternalService {

    SingleResponse<Boolean> getWorkingSatByDateOfficeId(LocalDate workingDate, Long officeId);
}
