package com.employee.LeaveService.controller;

import com.employee.LeaveService.dto.request.PutWorkingSatPayload;
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
        System.out.println(payload.toString());
        return workingSatService.saveWorkingSaturday(payload);
    }

    @GetMapping("")
    public SingleResponse<List<WorkingSatResponse>> getWorkingSaturdayByOfficeId(@RequestParam Long officeId,
                                                                                 @RequestParam Integer month,
                                                                                 @RequestParam Integer year){
        return workingSatService.getWorkingSaturdayByOfficeId(officeId,month,year);
    }

    @PutMapping("")
    public SingleResponse<WorkingSatResponse> updateWorkingSaturday(@RequestParam Long id, @Valid @RequestBody PutWorkingSatPayload payload){
        return workingSatService.updateWorkingSaturday(id,payload);
    }

    @DeleteMapping("/{id}")
    public SingleResponse<?> deleteWorkingSaturday(@PathVariable Long id){
        return workingSatService.deleteWorkingSaturday(id);
    }

}
