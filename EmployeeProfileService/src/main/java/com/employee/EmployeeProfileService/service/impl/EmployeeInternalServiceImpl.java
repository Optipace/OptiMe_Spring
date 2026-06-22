package com.employee.EmployeeProfileService.service.impl;

import com.employee.EmployeeProfileService.dto.request.CompleteProfileRequest;
import com.employee.EmployeeProfileService.dto.request.EmployeeProfileRequest;
import com.employee.EmployeeProfileService.dto.response.ApiResponse;
import com.employee.EmployeeProfileService.exception.CustomException;
import com.employee.EmployeeProfileService.model.Employee;
import com.employee.EmployeeProfileService.model.Office;
import com.employee.EmployeeProfileService.repository.EmployeeRepository;
import com.employee.EmployeeProfileService.repository.OfficeRepository;
import com.employee.EmployeeProfileService.service.EmployeeInternalService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class EmployeeInternalServiceImpl implements EmployeeInternalService {

    private final EmployeeRepository employeeRepository;

    private final OfficeRepository officeRepository;

    @Override
    public ApiResponse<?> createProfile(EmployeeProfileRequest request) {
        Employee newEmployee =  new Employee();
        newEmployee.setEmployeeId(request.getEmployeeId());
        newEmployee.setEmployeeName(request.getUserName());
        newEmployee.setContact(request.getContact());
        newEmployee.setEmailId(request.getEmailId());
        newEmployee.setDesignation(String.valueOf(request.getDesignation()));
        newEmployee.setRole(String.valueOf(request.getRole()));
        newEmployee.setGender(request.getGender());
        newEmployee.setWorkType(String.valueOf(request.getWorkType()));
        Office office = officeRepository.findById(request.getOfficeId())
                        .orElseThrow(() -> new CustomException("Office not found", HttpStatus.NOT_FOUND));
        newEmployee.setOffice(office);

        employeeRepository.save(newEmployee);
        return new ApiResponse<>(
                true,
                "Employee profile created",
                null,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<?> completeProfile(CompleteProfileRequest request) {

        System.out.println("Employee id is : "+request.getEmployeeId());
        Employee employee = employeeRepository.findEmployeeByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

        employee.setEmployeeName(request.getEmployeeName());
        employee.setAddress(request.getAddress());
        employee.setEmergencyContact(request.getEmergencyContact());
        employee.setDateOfBirth(request.getDateOfBirth());
        employeeRepository.save(employee);

        return new ApiResponse<>(
                true,
                "Employee saved successfully",
                null,
                LocalDateTime.now(),
                200
        );
    }
}
