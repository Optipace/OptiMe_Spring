package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.enums.CustomStatus;
import com.employee.LeaveService.repository.HolidayRepository;
import com.employee.LeaveService.service.HolidayInternalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@AllArgsConstructor
public class HolidayInternalImp implements HolidayInternalService {

    private final HolidayRepository holidayRepository;
    @Override
    public SingleResponse<Boolean> getHolidayByDateOfficeId(LocalDate holidayDate, Long officeId) {
        Boolean isPresent = holidayRepository.findByHolidayDateAndOfficeId(holidayDate,officeId).isPresent();
        return new SingleResponse<>(isPresent, CustomStatus.SUCCESS);
    }
}
