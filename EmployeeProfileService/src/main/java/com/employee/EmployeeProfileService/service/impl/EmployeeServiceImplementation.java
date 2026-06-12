package com.employee.EmployeeProfileService.service.impl;

import com.employee.EmployeeProfileService.dto.response.*;
import com.employee.EmployeeProfileService.exception.CustomException;
import com.employee.EmployeeProfileService.model.Employee;
import com.employee.EmployeeProfileService.repository.EmployeeRepository;
import com.employee.EmployeeProfileService.service.EmployeeService;
import com.employee.EmployeeProfileService.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImplementation implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    private final ModelMapper mapperModel;

    private final JwtUtil jwtUtil;

    @Override
    public ApiResponse<List<EmployeeResponse>> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeResponse> employeeResponse = employees.stream()
                .map(employee -> mapperModel.map(employee, EmployeeResponse.class))
                .toList();

        return new ApiResponse<>(
                true,
                "List of employees",
                employeeResponse,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<EmployeeResponse> getEmployeeDetails(String employeeId) {

//        Office office = null;
        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

//        if(employee.getOffice() != null){
//            office = officeRepository.findById(employee.getOffice().getId())
//                    .orElseThrow(() -> new CustomException("Something went wrong",HttpStatus.BAD_REQUEST));
//        }

        EmployeeResponse response = mapperModel.map(employee, EmployeeResponse.class);
//        if (office != null) {
//            OfficeResponse officeResponse = mapperModel.map(office, OfficeResponse.class);
//            response.setOffice(officeResponse);
//        } else {
//            response.setOffice(null);
//        }

        return new ApiResponse<>(
                true,
                "Employee Details",
                response,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<EmployeeResponse> getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

        EmployeeResponse response = mapperModel.map(employee, EmployeeResponse.class);
        return new ApiResponse<>(
                true,
                "Employee Details",
                response,
                LocalDateTime.now(),
                200
        );
    }
}
