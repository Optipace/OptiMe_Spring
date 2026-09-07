package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.model.Holidays;

import java.time.LocalDate;
import java.util.List;

public interface HolidayInternalService {
    SingleResponse<Boolean> getHolidayByDateOfficeId(LocalDate holidayDate, Long officeId);

    SingleResponse<List<Holidays>> getHolidayByMonthAndYear(Integer month, Integer year, Long officeId);
}
