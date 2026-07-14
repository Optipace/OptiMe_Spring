package com.employee.EmployeeProfileService.service.impl;

import com.employee.EmployeeProfileService.dto.request.CompleteProfileRequest;
import com.employee.EmployeeProfileService.dto.request.EmployeeProfileRequest;
import com.employee.EmployeeProfileService.dto.request.UpdateEmployeeStatusRequest;
import com.employee.EmployeeProfileService.dto.response.*;
import com.employee.EmployeeProfileService.enums.EmployeeDesignationEnum;
import com.employee.EmployeeProfileService.enums.EmployeeStatusEnum;
import com.employee.EmployeeProfileService.enums.RoleEnum;
import com.employee.EmployeeProfileService.enums.WorkTypeEnum;
import com.employee.EmployeeProfileService.exception.CustomException;
import com.employee.EmployeeProfileService.model.Employee;
import com.employee.EmployeeProfileService.repository.EmployeeRepository;
import com.employee.EmployeeProfileService.service.EmployeeInternalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class EmployeeInternalServiceImpl implements EmployeeInternalService {

    private final EmployeeRepository employeeRepository;

    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<?> createProfile(EmployeeProfileRequest request) {

        Employee newEmployee =  new Employee();
        newEmployee.setEmployeeId(request.getEmployeeId());
        newEmployee.setEmployeeName(request.getEmployeeName());
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
        newEmployee.setOfficeId(request.getOfficeId());

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

    @Override
    public ApiResponse<?> getMasterDetails(){

        List<EmployeeDesignationEnum> employeeDesignationEnumList = List.of(EmployeeDesignationEnum.values());
        List<RoleEnum> roleEnumList = List.of(RoleEnum.values());
        List<WorkTypeEnum> workTypeEnumList = List.of(WorkTypeEnum.values());
        List<EmployeeStatusEnum> employeeStatusEnumList = List.of(EmployeeStatusEnum.values());

        MasterEmployeeResponse masterEmployeeResponse = new MasterEmployeeResponse(employeeDesignationEnumList, roleEnumList, workTypeEnumList, employeeStatusEnumList);

        return new ApiResponse<>(
                true,
                "Master Response",
                masterEmployeeResponse,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public boolean checkEmployeeByEmployeeId(String employeeId) {
        boolean employeeExists = employeeRepository.existsByEmployeeId(employeeId);
        if(employeeExists){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public ApiResponse<?> updateEmployeeStatus(UpdateEmployeeStatusRequest request) {
        Employee employee = employeeRepository.findEmployeeByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new CustomException("Employee Id not found", HttpStatus.NOT_FOUND));

        employee.setEmployeeStatus(request.getEmployeeStatus());
        employeeRepository.save(employee);
        return new ApiResponse<>(
                true,
                "Employee status updated",
                null,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<EmployeeInternalResponse> getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException("Employee id not found", HttpStatus.NOT_FOUND));

        EmployeeInternalResponse response = modelMapper.map(employee, EmployeeInternalResponse.class);
        return new ApiResponse<>(
                true,
                "Employee details",
                response,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<?> deleteIdentity(String employeeId) {
        employeeRepository.findEmployeeByEmployeeId(employeeId).ifPresent( employee -> {
            log.info("Rollback executed: Employee {} deleted.", employeeId);
            employeeRepository.delete(employee);
        });
        return new ApiResponse<>(
                true,
                "Employee identity rollback processed",
                null,
                LocalDateTime.now(),
                200
        );
    }


}
