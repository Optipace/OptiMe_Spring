package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.enums.CustomStatus;
import com.employee.LeaveService.model.Holidays;
import com.employee.LeaveService.repository.HolidayRepository;
import com.employee.LeaveService.service.HolidayInternalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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

    @Override
    public SingleResponse<List<Holidays>> getHolidayByMonthAndYear(Integer month, Integer year, Long officeId) {
        LocalDate startDate=LocalDate.of(year,month,1);
        LocalDate endDate = startDate.withDayOfMonth(
                startDate.lengthOfMonth()
        );
        return new SingleResponse<>(holidayRepository.findByYearsFromTo(startDate,endDate,officeId).orElse(null)
                ,CustomStatus.SUCCESS);
    }
}
