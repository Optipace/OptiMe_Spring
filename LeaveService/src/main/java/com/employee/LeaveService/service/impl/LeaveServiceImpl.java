package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.client.EmployeeClient;
import com.employee.LeaveService.client.CommunicationClient;
import com.employee.LeaveService.dto.request.*;
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
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeClient employeeClient;
    private final ModelMapper modelMapper;
    private final CommunicationClient communicationClient;
    private final ObjectMapper objectMapper;

    @Override
    public SingleResponse<?> saveLeaveApplication(LeaveRequest request, String employeeId, String employeeName, String applicantEmailId) {
        if(request.getToDate().isBefore(request.getFromDate())){
            throw new CustomException(null, CustomStatus.INVALID_LEAVE_DATE_RANGE, 409);
        }

        // DUPLICATE CHECK: Verify if the employee already has a leave covering these dates
        boolean hasOverlap = leaveRepository.existsOverlappingLeave(employeeId, request.getFromDate(), request.getToDate());
        if (hasOverlap) {
            throw new CustomException(null, CustomStatus.DUPLICATE_LEAVE_APPLICATION, 409);
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
        EmployeeResponse authorityEmployeeResponse = employeeResponse.getData();
        Leave leave = new Leave();
        leave.setFromDate(request.getFromDate());
        leave.setToDate(request.getToDate());
        leave.setReason(request.getReason());
        leave.setApplicantEmployeeId(employeeId);
        leave.setAppliedOn(LocalDateTime.now());
        leave.setApplicantEmployeeName(employeeName);
        leave.setApproverEmpId(authorityEmployeeResponse.getEmployeeId());
        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                        .orElseThrow(() -> new CustomException(null, CustomStatus.LEAVE_TYPE_NOT_FOUND, 409));
        log.info("Leave type is {}", leaveType.getLeaveType().toUpperCase());
        leave.setLeaveType(leaveType);

        LeaveEmailPayload emailPayload = new LeaveEmailPayload(
                authorityEmployeeResponse.getEmployeeName(),
                employeeId,
                employeeName,
                authorityEmployeeResponse.getEmailId(),
                request.getFromDate(),
                request.getToDate(),
                request.getReason(),
                leaveType.getLeaveType()
        );
        ApiResponse<String> apiResponse;
        try {
            // For email service
            apiResponse = communicationClient.sendLeaveEmail(emailPayload);
            log.info("Triggered leave request email");
            log.info("Communication service is called to send leave email");

            // Sending private Notification to approver
            NotificationPayload approverPayload = new NotificationPayload();
            approverPayload.setEmployeeId(authorityEmployeeResponse.getEmployeeId());
            approverPayload.setTitle("New Leave Request");
            approverPayload.setMessage("You have an Leave request from employee: "+employeeName);
            approverPayload.setType("INFO");

            communicationClient.sendPrivateNotification(approverPayload);
            log.info("Notification is sent to approver employee {}",authorityEmployeeResponse.getEmployeeId());
        } catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                } else {
                    cleanErrorMessage = rawErrorJson;
                }
            } catch (Exception parseException) {
                // If the error isn't JSON, just return the raw string
                cleanErrorMessage = rawErrorJson;
            }
            throw new CustomException(cleanErrorMessage, HttpStatus.valueOf(e.status()));
        }

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
        if(apiResponse.getStatus() == 200){
            leaveRepository.save(leave);
        }

        LeaveConfirmationPayload payload = new LeaveConfirmationPayload(
                applicantEmailId,
                employeeName,
                leaveType.getLeaveType(),
                request.getFromDate(),
                request.getToDate()
        );

        try {
            // For email service
            communicationClient.sendConfirmationLeaveEmail(payload);
            log.info("Triggered leave confirmation email");
            log.info("Communication service is called to send confirmation leave email");

            // Sending private notification to applicant
            NotificationPayload applicantPayload = new NotificationPayload();
            applicantPayload.setEmployeeId(employeeId);
            applicantPayload.setTitle("Your Leave Request sent to "+authorityEmployeeResponse.getEmployeeId());
            applicantPayload.setMessage("You applied for the leave from "+request.getFromDate()+"  to "+request.getToDate());
            applicantPayload.setType("INFO");

            communicationClient.sendPrivateNotification(applicantPayload);
            log.info("Notification is sent to leave applicant{}", employeeId);

        } catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                } else {
                    cleanErrorMessage = rawErrorJson;
                }
            } catch (Exception parseException) {
                // If the error isn't JSON, just return the raw string
                cleanErrorMessage = rawErrorJson;
            }
            throw new CustomException(cleanErrorMessage, HttpStatus.valueOf(e.status()));
        }

        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> updateLeave(UpdateLeaveRequest request, String approvedEmployeeId) {
        Leave leave = leaveRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.LEAVE_ID_NOT_FOUND,409));

        if(leave.getApplicantEmployeeId().equals(approvedEmployeeId)){
            throw new CustomException(null, CustomStatus.UNAUTHORIZED_LEAVE_APPROVER, 409);
        }

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
            throw new CustomException(null, CustomStatus.UNAUTHORIZED_LEAVE_APPROVER, 409);
        }

        if(leave.getApprovedBy() == null || leave.getApprovedBy().isEmpty()){
            leave.setApprovedBy(empResponse.getData().getEmployeeId());
            leave.setLeaveStatus(request.getLeaveStatus());
            leaveRepository.save(leave);
        }
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }
}
