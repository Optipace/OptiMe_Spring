package com.employee.AdminService.controller;

import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.OfficeResponse;
import com.employee.AdminService.dto.response.SingleResponse;
import com.employee.AdminService.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/internal")
@RequiredArgsConstructor
public class InternalController {

    private final InternalService internalService;

    @GetMapping("/getOfficeList")
    public ResponseEntity<SingleResponse<List<OfficeResponse>>> getOfficeList(){
        SingleResponse<List<OfficeResponse>> response = internalService.getOfficeList();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getOfficeByOfficeId")
    public ResponseEntity<SingleResponse<OfficeResponse>> getOfficeDetailsByOfficeId(@RequestParam("officeId") Long officeId){
        SingleResponse<OfficeResponse> response = internalService.getOfficeDetailsByOfficeId(officeId);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getOfficeNames")
    public ResponseEntity<SingleResponse<?>> getOfficeNames(){
        SingleResponse<?> response = internalService.getOfficeNames();
        return ResponseEntity.status(200).body(response);
    }
}
