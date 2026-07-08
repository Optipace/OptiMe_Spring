package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.client.EmployeeClient;
import com.employee.LeaveService.dto.request.LeaveRequest;
import com.employee.LeaveService.dto.request.UpdateEmployeeStatusPayload;
import com.employee.LeaveService.dto.request.UpdateLeaveRequest;
import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.EmployeeResponse;
import com.employee.LeaveService.enums.EmployeeDesignationEnum;
import com.employee.LeaveService.enums.EmployeeStatusEnum;
import com.employee.LeaveService.enums.LeaveTypeEnum;
import com.employee.LeaveService.exception.CustomException;
import com.employee.LeaveService.model.Leave;
import com.employee.LeaveService.model.LeaveType;
import com.employee.LeaveService.repository.LeaveRepository;
import com.employee.LeaveService.repository.LeaveTypeRepository;
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
    private final LeaveTypeRepository leaveTypeRepository;
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
        log.info("Leave type is {}", request.getLeaveType());
        LeaveType leaveType = leaveTypeRepository.findByLeaveType(request.getLeaveType())
                        .orElseThrow(() -> new CustomException("Leave type not found", HttpStatus.NOT_FOUND));
        leave.setLeaveType(leaveType);
        leaveRepository.save(leave);

//        TODO: Need to send email to the employee and the respected authority

//        TODO: Need to be set automatically on the day his/her leave starts (Use Scheduler)
//        try{
//            UpdateEmployeeStatusPayload payload = new UpdateEmployeeStatusPayload(
//                    leave.getEmployeeId(),
//                    EmployeeStatusEnum.ON_LEAVE
//            );
//
//            employeeClient.updateEmployeeStatus(payload);
//            log.info("Employee Profile Service called to update employee status to LEAVE");
//
//        }catch (FeignException feignException){
//            String cleanErrorMessage = "Microservice called failed";
//            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//            if(feignException.status() > 0){
//                try {
//                    responseStatus = HttpStatus.valueOf(feignException.status());
//                } catch (IllegalArgumentException ex) {
//                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//                }
//            }else {
//                cleanErrorMessage = "Service is unreachable. Please try again later.";
//                responseStatus = HttpStatus.SERVICE_UNAVAILABLE;
//            }
//            throw new CustomException(cleanErrorMessage, responseStatus);
//        }
        return new ApiResponse<>(
                true,
                "Leave applied successfully",
                null,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<?> updateLeave(UpdateLeaveRequest request, String approvedEmployeeId) {
        Leave leave = leaveRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new CustomException("Leave Id not found", HttpStatus.NOT_FOUND));

        ApiResponse<EmployeeResponse> empResponse;
        try{

            log.info("Calling Employee Profile Service for {} details",approvedEmployeeId);
            empResponse = employeeClient.getEmployeeByEmployeeId(approvedEmployeeId);
            log.info("Employee {} details got", approvedEmployeeId);

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
            log.error("Employee Service unreachable");
            throw new CustomException(cleanErrorMessage, responseStatus);
        }

        EmployeeDesignationEnum approverRole = EmployeeDesignationEnum.valueOf(empResponse.getData().getEmployeeDesignation());
        if(!approverRole.canApproveLeave()) {
            throw new CustomException("You are Unauthorised to perform this action!", HttpStatus.UNAUTHORIZED);
        }

        if(leave.getApprovedBy() == null || leave.getApprovedBy().isEmpty()){
            leave.setApprovedBy(empResponse.getData().getEmployeeId());
            leave.setLeaveStatus(request.getLeaveStatus());
            leave = leaveRepository.save(leave);
        }else {
            return new ApiResponse<>(
                    true,
                    "Leave already "+leave.getLeaveStatus()+" by: "+leave.getApprovedBy(),
                    null,
                    LocalDateTime.now(),
                    200
            );
        }
        return new ApiResponse<>(
                true,
                "Leave "+leave.getLeaveStatus(),
                null,
                LocalDateTime.now(),
                200
        );
    }
}
