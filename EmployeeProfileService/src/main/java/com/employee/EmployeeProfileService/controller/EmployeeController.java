package com.employee.EmployeeProfileService.controller;


import com.employee.EmployeeProfileService.dto.response.*;
import com.employee.EmployeeProfileService.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService empService;

    @GetMapping("/allEmp")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAllEmployees(){
        ApiResponse<List<EmployeeResponse>> response = empService.getAllEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getEmployeeDetails")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeDetails(@RequestHeader("X-Employee-Id") String employeeId){
        ApiResponse<EmployeeResponse> response = empService.getEmployeeDetails(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getEmployeeByID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByEmployeeId(@RequestParam("employeeId")String employeeId){
        ApiResponse<EmployeeResponse> response = empService.getEmployeeByEmployeeId(employeeId);
        return ResponseEntity.status((HttpStatus.OK)).body(response);
    }
}