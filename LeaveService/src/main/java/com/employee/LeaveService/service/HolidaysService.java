package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.request.PutHolidayRequest;
import com.employee.LeaveService.dto.request.SaveHolidaysRequest;
import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.model.Holidays;

import java.util.List;

public interface HolidaysService {
    SingleResponse<?> saveHolidays(List<SaveHolidaysRequest> holidays);

    SingleResponse<?> getHolidaysOnYears(Integer yearFrom, Integer yearTo,Long officeId);

    SingleResponse<?> updateHolidayById(Long id, PutHolidayRequest request);

    SingleResponse<?> deleteHolidayById(Long id);
}
