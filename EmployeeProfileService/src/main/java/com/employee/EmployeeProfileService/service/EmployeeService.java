package com.employee.EmployeeProfileService.service;

import com.employee.EmployeeProfileService.dto.response.*;

import java.util.List;

public interface EmployeeService {
    public ApiResponse<List<EmployeeResponse>> getAllEmployees();

    public ApiResponse<EmployeeResponse> getEmployeeDetails(String employeeId);

    public ApiResponse<EmployeeResponse> getEmployeeByEmployeeId(String employeeId);
}
