package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.response.SingleResponse;

import java.time.LocalDate;

public interface HolidayInternalService {
    SingleResponse<Boolean> getHolidayByDateOfficeId(LocalDate holidayDate, Long officeId);
}
