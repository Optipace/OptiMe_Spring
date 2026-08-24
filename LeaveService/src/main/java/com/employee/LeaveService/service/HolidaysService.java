package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.request.SaveHolidaysRequest;
import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.model.Holidays;

import java.util.List;

public interface HolidaysService {
    SingleResponse<?> saveHolidays(List<SaveHolidaysRequest> holidays);

    SingleResponse<?> getHolidaysOnYears(Integer yearFrom, Integer yearTo);

    SingleResponse<?> updateHolidayById(Long id,SaveHolidaysRequest request);

    SingleResponse<?> deleteHolidayById(Long id);
}
