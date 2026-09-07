package com.employee.LeaveService.controller;

import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.model.Holidays;
import com.employee.LeaveService.service.HolidayInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave/internal")
@RequiredArgsConstructor
public class HolidayInternalController {
    private final HolidayInternalService holidayInternalService;

    @GetMapping("/checkHoliday")
    public SingleResponse <Boolean> getHolidayByDateOfficeId(@RequestParam LocalDate holidayDate,@RequestParam Long officeId){
        return holidayInternalService.getHolidayByDateOfficeId(holidayDate,officeId);
    }
    @GetMapping("/getHolidayByMonthAndYear")
    public SingleResponse <List<Holidays>> getHolidayByMonthAndYear(@RequestParam Integer month, @RequestParam Integer year, @RequestParam Long officeId){
        return holidayInternalService.getHolidayByMonthAndYear(month,year,officeId);
    }
}
