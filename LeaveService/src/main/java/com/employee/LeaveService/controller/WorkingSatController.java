package com.employee.LeaveService.controller;

import com.employee.LeaveService.dto.request.WorkingSatPayload;
import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.dto.response.WorkingSatResponse;
import com.employee.LeaveService.service.WorkingSatService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave/workingSaturday")
@AllArgsConstructor
public class WorkingSatController {

    private final WorkingSatService workingSatService;

    @PostMapping("save")
    public SingleResponse<?> saveWorkingSaturday(@Valid @RequestBody WorkingSatPayload payload){
        return workingSatService.saveWorkingSaturday(payload);
    }

    @GetMapping("")

    public SingleResponse<List<WorkingSatResponse>> getWorkingSaturdayByOfficeId(@RequestParam Long officeId){
        return workingSatService.getWorkingSaturdayByOfficeId(officeId);
    }
}
