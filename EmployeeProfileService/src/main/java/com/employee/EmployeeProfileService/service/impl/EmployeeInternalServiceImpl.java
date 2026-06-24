package com.employee.EmployeeProfileService.service.impl;

import com.employee.EmployeeProfileService.dto.request.CompleteProfileRequest;
import com.employee.EmployeeProfileService.dto.request.EmployeeProfileRequest;
import com.employee.EmployeeProfileService.dto.response.ApiResponse;
import com.employee.EmployeeProfileService.dto.response.EmployeeResponse;
import com.employee.EmployeeProfileService.enums.EmployeeStatusEnum;
import com.employee.EmployeeProfileService.exception.CustomException;
import com.employee.EmployeeProfileService.model.Employee;
import com.employee.EmployeeProfileService.model.Office;
import com.employee.EmployeeProfileService.repository.EmployeeRepository;
import com.employee.EmployeeProfileService.repository.OfficeRepository;
import com.employee.EmployeeProfileService.service.EmployeeInternalService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class EmployeeInternalServiceImpl implements EmployeeInternalService {

    private final EmployeeRepository employeeRepository;

    private final OfficeRepository officeRepository;

    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<?> createProfile(EmployeeProfileRequest request) {

        Employee newEmployee =  new Employee();
        newEmployee.setEmployeeId(request.getEmployeeId());
        newEmployee.setEmployeeName(request.getUserName());
        newEmployee.setContact(request.getContact());
        newEmployee.setEmailId(request.getEmailId());
        newEmployee.setDesignation(request.getDesignation());

        if(String.valueOf(request.getRole()).equals("ADMIN")){
            newEmployee.setRole(request.getRole());
        }
        newEmployee.setRole(request.getRole());
        newEmployee.setGender(request.getGender());
        newEmployee.setWorkType(request.getWorkType());
        newEmployee.setDateOfBirth(request.getDateOfBirth());
        newEmployee.setProfileStatus(4);
        newEmployee.setDateOfJoining(request.getDateOfJoining());
        newEmployee.setPermanentAddress(request.getPermanentAddress());
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

        Employee employee = employeeRepository.findEmployeeByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

        int currentStatus = employee.getProfileStatus();

        if(currentStatus == 6 || currentStatus == 7){
            throw new CustomException("Profile already completed please login", HttpStatus.BAD_REQUEST);
        }
        employee.setCurrentAddress(request.getCurrentAddress());
        employee.setEmergencyContact(request.getEmergencyContact());
        employee.setBloodGroup(request.getBloodGroup());
//        employee.setProfileStatus(ProfileStatusEnum.COMPLETE);
        int result = currentStatus | 2;
        employee.setProfileStatus(result);
        employee.setEmployeeStatus(EmployeeStatusEnum.ACTIVE);
        employeeRepository.save(employee);

        return new ApiResponse<>(
                true,
                "Employee saved successfully",
                null,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<EmployeeResponse> getProfile(String employeeId) {
        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

        EmployeeResponse response = modelMapper.map(employee,EmployeeResponse.class);
        return new ApiResponse<>(
                true,
                "Employee details",
                response,
                LocalDateTime.now(),
                200
        );
    }
}
