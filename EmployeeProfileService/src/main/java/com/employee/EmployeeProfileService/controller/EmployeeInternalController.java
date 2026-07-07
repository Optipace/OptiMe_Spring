package com.employee.EmployeeProfileService.controller;

import com.employee.EmployeeProfileService.dto.request.CompleteProfileRequest;
import com.employee.EmployeeProfileService.dto.request.EmployeeProfileRequest;
import com.employee.EmployeeProfileService.dto.request.UpdateEmployeeStatusRequest;
import com.employee.EmployeeProfileService.dto.response.ApiResponse;
import com.employee.EmployeeProfileService.dto.response.EmployeeInternalResponse;
import com.employee.EmployeeProfileService.dto.response.EmployeeResponse;
import com.employee.EmployeeProfileService.service.EmployeeInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee/internal")
@RequiredArgsConstructor
public class EmployeeInternalController {

    private final EmployeeInternalService empInternalService;

    @PostMapping("/create-profile")
    public ResponseEntity<ApiResponse<?>> createProfile(@RequestBody EmployeeProfileRequest request){
        ApiResponse<?> response = empInternalService.createProfile(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/get-profile")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getProfile(@RequestParam String employeeId){
        ApiResponse<EmployeeResponse> response = empInternalService.getProfile(employeeId);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<ApiResponse<?>> completeProfile(@RequestBody CompleteProfileRequest request){
        ApiResponse<?> response = empInternalService.completeProfile(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getMasterDetails")
    public ResponseEntity<ApiResponse<?>> getMasterDetails(){
        ApiResponse<?> response = empInternalService.getMasterDetails();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/checkEmployeeByEmployeeId")
    public boolean checkEmployeeByEmployeeId(@RequestParam("employeeId") String employeeId){
        return empInternalService.checkEmployeeByEmployeeId(employeeId);
    }

    @PostMapping("/updateEmployeeStatus")
    public ResponseEntity<ApiResponse<?>> updateEmployeeStatus(@RequestBody UpdateEmployeeStatusRequest request) {
        ApiResponse<?> response = empInternalService.updateEmployeeStatus(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getEmployeeByEmployeeId")
    public ResponseEntity<ApiResponse<EmployeeInternalResponse>> getEmployeeByEmployeeId(@RequestParam("employeeId")String employeeId){
        ApiResponse<EmployeeInternalResponse> response = empInternalService.getEmployeeByEmployeeId(employeeId);
        return ResponseEntity.ok(response);
    }

}
