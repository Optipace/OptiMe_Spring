package com.employee.LeaveService.controller;

import com.employee.LeaveService.dto.request.PutHolidayRequest;
import com.employee.LeaveService.dto.request.SaveHolidaysRequest;
import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.model.Holidays;
import com.employee.LeaveService.service.HolidaysService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidaysService holidaysService;

    @PostMapping("/save")
    public SingleResponse<?> saveHolidays(@RequestBody List<SaveHolidaysRequest> holidays){
        return holidaysService.saveHolidays(holidays);
    }

    @GetMapping("")
    public SingleResponse<?> getHolidays(@RequestParam Integer yearFrom,
                                         @RequestParam Integer yearTo,
                                         @RequestParam Long officeId){
        return holidaysService.getHolidaysOnYears(yearFrom,yearTo,officeId);
    }

    @GetMapping("/getByMonthAndYear")
    public SingleResponse<?> getHolidaysByMonthAndYear(@RequestParam Integer month,
                                         @RequestParam Integer year,
                                         @RequestParam Long officeId){
        return holidaysService.getHolidaysByMonthYear(month,year,officeId);
    }

    @PutMapping("")
    public SingleResponse<?> updateHoliday(@RequestParam Long id,
                                           @RequestBody PutHolidayRequest request){
        return holidaysService.updateHolidayById(id,request);
    }
    @DeleteMapping("/{id}")
    public SingleResponse<?> deleteHoliday(@PathVariable Long id){
        return holidaysService.deleteHolidayById(id);
    }
}
