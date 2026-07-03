package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.client.EmployeeClient;
import com.employee.LeaveService.dto.request.LeaveRequest;
import com.employee.LeaveService.dto.request.UpdateEmployeeStatusPayload;
import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.enums.EmployeeStatusEnum;
import com.employee.LeaveService.exception.CustomException;
import com.employee.LeaveService.model.Leave;
import com.employee.LeaveService.repository.LeaveRepository;
import com.employee.LeaveService.service.LeaveService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeClient employeeClient;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<?> saveLeaveApplication(LeaveRequest request, String employeeId, String employeeName) {

        if(request.getToDate().isBefore(request.getFromDate())){
            throw new CustomException("The 'To Date' cannot be earlier than the 'From Date'", HttpStatus.BAD_REQUEST);
        }

        boolean employeeExists = false;
        try{
            employeeExists = employeeClient.checkEmployeeByEmployeeId(employeeId);
            log.info("Checking employee exists");
        }catch (FeignException feignException){
            String cleanErrorMessage = "Microservice called failed";
            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if(feignException.status() > 0){
                try {
                    responseStatus = HttpStatus.valueOf(feignException.status());
                } catch (IllegalArgumentException ex) {
                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            }else {
                cleanErrorMessage = "Service is unreachable. Please try again later.";
                responseStatus = HttpStatus.SERVICE_UNAVAILABLE;
            }
            throw new CustomException(cleanErrorMessage, responseStatus);
        }

        if(!employeeExists){
            throw new CustomException("Employee Id not found", HttpStatus.NOT_FOUND);
        }

        Leave leave = modelMapper.map(request, Leave.class);
        leave.setEmployeeId(employeeId);
        leave.setAppliedOn(LocalDateTime.now());
        leave.setEmployeeName(employeeName);
        leaveRepository.save(leave);

        try{
            UpdateEmployeeStatusPayload payload = new UpdateEmployeeStatusPayload(
                    leave.getEmployeeId(),
                    EmployeeStatusEnum.ON_LEAVE
            );

            employeeClient.updateEmployeeStatus(payload);
            log.info("Employee Profile service called to update employee status to LEAVE");

        }catch (FeignException feignException){
            String cleanErrorMessage = "Microservice called failed";
            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if(feignException.status() > 0){
                try {
                    responseStatus = HttpStatus.valueOf(feignException.status());
                } catch (IllegalArgumentException ex) {
                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            }else {
                cleanErrorMessage = "Service is unreachable. Please try again later.";
                responseStatus = HttpStatus.SERVICE_UNAVAILABLE;
            }
            throw new CustomException(cleanErrorMessage, responseStatus);
        }
        return new ApiResponse<>(
                true,
                "Leave applied successfully",
                null,
                LocalDateTime.now(),
                200
        );
    }
}
