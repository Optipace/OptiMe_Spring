package com.employee.EmployeeProfileService.controller;

import com.employee.EmployeeProfileService.dto.request.CompleteProfileRequest;
import com.employee.EmployeeProfileService.dto.request.EmployeeProfileRequest;
import com.employee.EmployeeProfileService.dto.response.ApiResponse;
import com.employee.EmployeeProfileService.service.EmployeeInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/employee/internal")
@RequiredArgsConstructor
public class EmployeeInternalController {

    private final EmployeeInternalService empInternalService;

    @PostMapping("/create-profile")
    public ResponseEntity<ApiResponse<?>> createProfile(@RequestBody EmployeeProfileRequest request){
        ApiResponse<?> response = empInternalService.createProfile(request);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<ApiResponse<?>> completeProfile(@RequestBody CompleteProfileRequest request){
        ApiResponse<?> response = empInternalService.completeProfile(request);
        return ResponseEntity.status(200).body(response);
    }

}
