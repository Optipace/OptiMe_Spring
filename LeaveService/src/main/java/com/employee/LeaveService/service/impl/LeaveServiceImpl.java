package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.client.EmployeeClient;
import com.employee.LeaveService.dto.request.LeaveRequest;
import com.employee.LeaveService.dto.request.UpdateLeaveRequest;
import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.EmployeeResponse;
import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.enums.CustomStatus;
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
    public SingleResponse<?> saveLeaveApplication(LeaveRequest request, String employeeId, String employeeName) {

        if(request.getToDate().isBefore(request.getFromDate())){
            throw new CustomException("The 'To Date' cannot be earlier than the 'From Date'", HttpStatus.BAD_REQUEST);
        }

       ApiResponse<EmployeeResponse> employeeResponse;
        try{
            employeeResponse = employeeClient.getEmployeeByEmployeeId(request.getAuthorityEmployeeId());
            log.info("Employee details fetched");
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
        EmployeeResponse response = employeeResponse.getData();
        Leave leave = new Leave();
        leave.setFromDate(request.getFromDate());
        leave.setToDate(request.getToDate());
        leave.setReason(request.getReason());
        leave.setApplicantEmployeeId(employeeId);
        leave.setAppliedOn(LocalDateTime.now());
        leave.setApplicantEmployeeName(employeeName);
        leave.setApproverEmpId(response.getEmployeeId());
        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                        .orElseThrow(() -> new CustomException("Leave type not found", HttpStatus.NOT_FOUND));
        log.info("Leave type is {}", leaveType.getLeaveType().toUpperCase());
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
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> updateLeave(UpdateLeaveRequest request, String approvedEmployeeId) {
        Leave leave = leaveRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new CustomException("Leave Id not found", HttpStatus.NOT_FOUND));

        ApiResponse<EmployeeResponse> empResponse;
        try{

            log.info("Calling Employee Profile Service for {} details",approvedEmployeeId);
            empResponse = employeeClient.getEmployeeByEmployeeId(approvedEmployeeId);
            log.info("Employee {} details got", approvedEmployeeId);

        }catch (FeignException feignException) {
            String cleanErrorMessage = "Microservice called failed";
            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if (feignException.status() > 0) {
                try {
                    responseStatus = HttpStatus.valueOf(feignException.status());
                } catch (IllegalArgumentException ex) {
                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            } else {
                cleanErrorMessage = "Service is unreachable. Please try again later.";
                responseStatus = HttpStatus.SERVICE_UNAVAILABLE;
            }
            log.error("Employee Service unreachable");
            throw new CustomException(cleanErrorMessage, responseStatus);
        }

//        if(){
//         // TODO The higher authority can't approve their leave by themselves
//        }
        if(!empResponse.getData().isCanApproveLeave()) {
            throw new CustomException("Only higher authorities can approve leave!", HttpStatus.UNAUTHORIZED);
        }

        if(leave.getApprovedBy() == null || leave.getApprovedBy().isEmpty()){
            leave.setApprovedBy(empResponse.getData().getEmployeeId());
            leave.setLeaveStatus(request.getLeaveStatus());
            leave = leaveRepository.save(leave);
        }
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }
}
