package com.employee.LeaveService.controller;

import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.service.WorkingSatInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/leave/internal")
@RequiredArgsConstructor
public class WorkingSatInternalController {

    private final WorkingSatInternalService workingSatInternalService;

    @GetMapping("/checkWorkingSaturday")
    public SingleResponse<Boolean> getWorkingSatByDateOfficeId(@RequestParam LocalDate workingDate,@RequestParam Long officeId){
        return workingSatInternalService.getWorkingSatByDateOfficeId(workingDate,officeId);
    }
}
