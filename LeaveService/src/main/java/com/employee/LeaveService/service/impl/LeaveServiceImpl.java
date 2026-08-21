package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.client.EmployeeClient;
import com.employee.LeaveService.client.CommunicationClient;
import com.employee.LeaveService.dto.request.*;
import com.employee.LeaveService.dto.response.*;
import com.employee.LeaveService.enums.CustomStatus;
import com.employee.LeaveService.enums.LeaveStatusEnum;
import com.employee.LeaveService.exception.CustomException;
import com.employee.LeaveService.model.AvailableLeaves;
import com.employee.LeaveService.model.Leave;
import com.employee.LeaveService.model.LeaveType;
import com.employee.LeaveService.repository.AvailableLeavesRepository;
import com.employee.LeaveService.repository.LeaveRepository;
import com.employee.LeaveService.repository.LeaveTypeRepository;
import com.employee.LeaveService.service.LeaveService;
import com.sun.net.httpserver.Authenticator;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;

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
    private final AvailableLeavesRepository availableLeavesRepository;

    @Override
    @Transactional
    public SingleResponse<?> saveLeaveApplication(LeaveRequest request, String empId, String applicantEmployeeId, String employeeName, String applicantEmailId) {

        Long employeeId = Long.parseLong(empId);

        if(request.getToDate().isBefore(request.getFromDate())){
            throw new CustomException(null, CustomStatus.INVALID_DATE_RANGE, 400);
        }

        // DUPLICATE CHECK: Verify if the employee already has a leave covering these dates
        boolean hasOverlap = leaveRepository.existsOverlappingLeave(employeeId, request.getFromDate(), request.getToDate());
        if (hasOverlap) {
            throw new CustomException(null, CustomStatus.DUPLICATE_LEAVE_APPLICATION, 409);
        }

       SingleResponse<EmployeeResponse> employeeResponse;
        try{
            employeeResponse = employeeClient.getEmployeeByEmployeeId(request.getAuthorityEmployeeId());
            log.info("Employee details fetched");
        }catch (FeignException e){
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";
            int extractedErrorCode = -100; // Defaults to MICROSERVICE_CALL_FAILED code

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);

                // Navigate inside the nested "response" block
                if (errorNode.has("response")) {
                    JsonNode responseNode = errorNode.get("response");
                    if (responseNode.has("message")) {
                        cleanErrorMessage = responseNode.get("message").asText();
                    }
                    if (responseNode.has("code")) {
                        extractedErrorCode = responseNode.get("code").asInt();
                    }
                } else if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            int httpStatusValue = (e.status() > 0) ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();

            // Map the integer code to the correct Enum instance safely
            CustomStatus status = CustomStatus.fromCode(extractedErrorCode);

            // Pass the clean extracted message to CustomException
            throw new CustomException(cleanErrorMessage, status, httpStatusValue);
        }
        EmployeeResponse authorityEmployeeResponse = employeeResponse.getData();
        Leave leave = new Leave();
        leave.setFromDate(request.getFromDate());
        leave.setToDate(request.getToDate());
        leave.setLeaveReason(request.getReason());
        leave.setApplicantEmployeeId(employeeId);
        leave.setAppliedOn(LocalDateTime.now());
        leave.setWantedLeaves(request.getRequestedLeaves());
//        leave.setApplicantEmployeeName(employeeName);
        leave.setApproverEmpId(authorityEmployeeResponse.getId());
        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                        .orElseThrow(() -> new CustomException(null, CustomStatus.LEAVE_TYPE_NOT_FOUND, 200));
        log.info("Leave type is {}", leaveType.getLeaveType().toUpperCase());
        leave.setLeaveType(leaveType);

        LeaveEmailPayload emailPayload = new LeaveEmailPayload(
                authorityEmployeeResponse.getEmployeeName(),
                applicantEmployeeId,
                employeeName,
                authorityEmployeeResponse.getEmailId(),
                request.getFromDate(),
                request.getToDate(),
                request.getReason(),
                leaveType.getLeaveType()
        );

        leaveRepository.save(leave);

        log.info("Leave saved successfully with id {}", leave.getId());

        SingleResponse<String> apiResponse;
        try {
            // For email service
            apiResponse = communicationClient.sendLeaveEmail(emailPayload);
            log.info("Triggered leave request email");
            log.info("Communication service is called to send leave email");

            // Sending private Notification to approver
            NotificationPayload approverPayload = new NotificationPayload();
            approverPayload.setEmployeeId(authorityEmployeeResponse.getId());
            approverPayload.setTitle("New Leave Request");
            approverPayload.setMessage("You have an Leave request from employee: "+employeeName);
            approverPayload.setType("INFO");

            communicationClient.sendPrivateNotification(approverPayload);
            log.info("Notification is sent to approver employee {}",authorityEmployeeResponse.getEmployeeId());
        } catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";
            int extractedErrorCode = -100; // Defaults to MICROSERVICE_CALL_FAILED code

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);

                // Navigate inside the nested "response" block
                if (errorNode.has("response")) {
                    JsonNode responseNode = errorNode.get("response");
                    if (responseNode.has("message")) {
                        cleanErrorMessage = responseNode.get("message").asText();
                    }
                    if (responseNode.has("code")) {
                        extractedErrorCode = responseNode.get("code").asInt();
                    }
                } else if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            int httpStatusValue = (e.status() > 0) ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();

            // Map the integer code to the correct Enum instance safely
            CustomStatus status = CustomStatus.fromCode(extractedErrorCode);

            // Pass the clean extracted message to CustomException
            throw new CustomException(cleanErrorMessage, status, httpStatusValue);
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
//        log.info("The api status response is :{}",apiResponse.getStatus());
//        if(apiResponse.getStatus() != 200){
//            throw new CustomException(null, CustomStatus.UNKNOWN, 500);
//        }
//        leaveRepository.save(leave);
//      availableLeavesRepository.save(availableLeaves);

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
            int extractedErrorCode = -100; // Defaults to MICROSERVICE_CALL_FAILED code

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);

                // Navigate inside the nested "response" block
                if (errorNode.has("response")) {
                    JsonNode responseNode = errorNode.get("response");
                    if (responseNode.has("message")) {
                        cleanErrorMessage = responseNode.get("message").asText();
                    }
                    if (responseNode.has("code")) {
                        extractedErrorCode = responseNode.get("code").asInt();
                    }
                } else if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            int httpStatusValue = (e.status() > 0) ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();

            // Map the integer code to the correct Enum instance safely
            CustomStatus status = CustomStatus.fromCode(extractedErrorCode);

            // Pass the clean extracted message to CustomException
            throw new CustomException(cleanErrorMessage, status, httpStatusValue);
        }
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public SingleResponse<?> approveLeave(ApproveLeaveRequest request, String authorityId) {

        Leave leave = leaveRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.LEAVE_ID_NOT_FOUND,404));

        AvailableLeaves availableLeaves = availableLeavesRepository.findByEmployeeId(leave.getApplicantEmployeeId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_LEAVE_BALANCE_RECORD_NOT_FOUND, 200));

        if(leave.getApplicantEmployeeId().equals(Long.parseLong(authorityId))){
            throw new CustomException(null, CustomStatus.UNAUTHORIZED_LEAVE_APPROVER, 200);
        }

        if(!leave.getLeaveStatus().equals(LeaveStatusEnum.PENDING)){
            throw new CustomException(null, CustomStatus.LEAVE_ALREADY_PROCESSED, 200);
        }

        SingleResponse<EmployeeResponse> authorityResponse;
        SingleResponse<EmployeeResponse> employeeResponse;
        try{

            log.info("Calling Employee Profile Service for {} details", authorityId);
            authorityResponse = employeeClient.getEmployeeByEmployeeId(Long.parseLong(authorityId));
            log.info("Employee {} details got", authorityId);

            log.info("Calling Employee Profile Service for {} details", leave.getApplicantEmployeeId());
            employeeResponse = employeeClient.getEmployeeByEmployeeId(leave.getApplicantEmployeeId());
            log.info("Employee {} details got", leave.getApplicantEmployeeId());

        }catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";
            int extractedErrorCode = -100; // Defaults to MICROSERVICE_CALL_FAILED code

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);

                // Navigate inside the nested "response" block
                if (errorNode.has("response")) {
                    JsonNode responseNode = errorNode.get("response");
                    if (responseNode.has("message")) {
                        cleanErrorMessage = responseNode.get("message").asText();
                    }
                    if (responseNode.has("code")) {
                        extractedErrorCode = responseNode.get("code").asInt();
                    }
                } else if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            int httpStatusValue = (e.status() > 0) ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();

            // Map the integer code to the correct Enum instance safely
            CustomStatus status = CustomStatus.fromCode(extractedErrorCode);

            // Pass the clean extracted message to CustomException
            throw new CustomException(cleanErrorMessage, status, httpStatusValue);
        }

//        if(){
//         // TODO The higher authority can't approve their leave by themselves
//        }
        if(!authorityResponse.getData().isCanApproveLeave() && !leave.getApproverEmpId().equals(Long.parseLong(authorityId))) {
            throw new CustomException(null, CustomStatus.UNAUTHORIZED_LEAVE_APPROVER, 200);
        }

        if(leave.getApprovedBy() == null){
            leave.setApprovedBy(authorityResponse.getData().getId());
            leave.setLeaveStatus(LeaveStatusEnum.APPROVED);
            if(StringUtils.hasText(request.getRemarks()) ){
                leave.setRemarks(request.getRemarks().trim());
            }
            int updatedBalance = availableLeaves.getRemainingLeaves() - leave.getWantedLeaves();
            availableLeaves.setEmployeeId(leave.getApplicantEmployeeId());
            availableLeaves.setRemainingLeaves(updatedBalance);

        }
        if(employeeResponse.getData() == null){
            throw new CustomException(null, CustomStatus.MICROSERVICE_CALL_FAILED, 200);
        }
        LeaveApprovePayload payload = new LeaveApprovePayload();
        payload.setEmployeeEmailId(employeeResponse.getData().getEmailId());
        payload.setEmployeeName(employeeResponse.getData().getEmployeeName());
        payload.setManagerName(authorityResponse.getData().getEmployeeName());
        payload.setLeaveType(leave.getLeaveType().getLeaveType());
        payload.setFromDate(leave.getFromDate());
        payload.setToDate(leave.getToDate());
        payload.setApprovalRemarks(leave.getRemarks() != null ? leave.getRemarks() : null);

        try {
            communicationClient.sendLeaveApprovedEmail(payload);
            log.info("Communication service is called to send approved leave email");

            NotificationPayload applicantPayload = new NotificationPayload(
                    leave.getApplicantEmployeeId(),
                    "Leave Status",
                    "The leave you applied on from "+leave.getFromDate()+" to "+leave.getToDate()+" for "+leave.getWantedLeaves()+"day(s) is APPROVED By "+authorityResponse.getData().getEmployeeName(),
                    "INFO"
            );
            communicationClient.sendPrivateNotification(applicantPayload);// TODO send at last


        } catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";
            int extractedErrorCode = -100; // Defaults to MICROSERVICE_CALL_FAILED code

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);

                // Navigate inside the nested "response" block
                if (errorNode.has("response")) {
                    JsonNode responseNode = errorNode.get("response");
                    if (responseNode.has("message")) {
                        cleanErrorMessage = responseNode.get("message").asText();
                    }
                    if (responseNode.has("code")) {
                        extractedErrorCode = responseNode.get("code").asInt();
                    }
                } else if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            int httpStatusValue = (e.status() > 0) ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();

            // Map the integer code to the correct Enum instance safely
            CustomStatus status = CustomStatus.fromCode(extractedErrorCode);

            // Pass the clean extracted message to CustomException
            throw new CustomException(cleanErrorMessage, status, httpStatusValue);
        }

        availableLeavesRepository.save(availableLeaves);
        leaveRepository.save(leave);

        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    @Transactional(rollbackFor = CustomException.class)
    public SingleResponse<?> rejectLeave(RejectLeaveRequest request, String authorityId) {

        Leave leave = leaveRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.LEAVE_ID_NOT_FOUND, 404));

        if(leave.getApplicantEmployeeId().equals(Long.parseLong(authorityId))){
            throw new CustomException(null, CustomStatus.UNAUTHORIZED_LEAVE_APPROVER, 200);
        }

        if(!leave.getLeaveStatus().equals(LeaveStatusEnum.PENDING)){
            throw new CustomException(null, CustomStatus.LEAVE_ALREADY_PROCESSED, 200);
        }

        SingleResponse<EmployeeResponse> authorityResponse;
        SingleResponse<EmployeeResponse> employeeResponse;
        try{

            log.info("Calling Employee Profile Service for {} details", authorityId);
            authorityResponse = employeeClient.getEmployeeByEmployeeId(Long.parseLong(authorityId));
            log.info("Employee {} details got", authorityId);

            log.info("Calling Employee Profile Service for {} details", leave.getApplicantEmployeeId());
            employeeResponse = employeeClient.getEmployeeByEmployeeId(leave.getApplicantEmployeeId());
            log.info("Employee {} details got", leave.getApplicantEmployeeId());

        }catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";
            int extractedErrorCode = -100; // Defaults to MICROSERVICE_CALL_FAILED code

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);

                // Navigate inside the nested "response" block
                if (errorNode.has("response")) {
                    JsonNode responseNode = errorNode.get("response");
                    if (responseNode.has("message")) {
                        cleanErrorMessage = responseNode.get("message").asText();
                    }
                    if (responseNode.has("code")) {
                        extractedErrorCode = responseNode.get("code").asInt();
                    }
                } else if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            int httpStatusValue = (e.status() > 0) ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();

            // Map the integer code to the correct Enum instance safely
            CustomStatus status = CustomStatus.fromCode(extractedErrorCode);

            // Pass the clean extracted message to CustomException
            throw new CustomException(cleanErrorMessage, status, httpStatusValue);
        }

//        if(){
//         // TODO The higher authority can't approve their leave by themselves
//        }
        if(!authorityResponse.getData().isCanApproveLeave() && !leave.getApproverEmpId().equals(Long.parseLong(authorityId))) {
            throw new CustomException(null, CustomStatus.UNAUTHORIZED_LEAVE_APPROVER, 200);
        }

        if(leave.getApprovedBy() == null){
            leave.setApprovedBy(authorityResponse.getData().getId());
            leave.setLeaveStatus(LeaveStatusEnum.DENIED);
            if(StringUtils.hasText(request.getRejectionReason())){
                leave.setRemarks(request.getRejectionReason().trim());
            }

            leaveRepository.save(leave);
        }

        LeaveRejectedPayload payload = new LeaveRejectedPayload();
        payload.setEmployeeEmailId(employeeResponse.getData().getEmailId());
        payload.setEmployeeName(employeeResponse.getData().getEmployeeName());
        payload.setManagerName(authorityResponse.getData().getEmployeeName());
        payload.setLeaveType(leave.getLeaveType().getLeaveType());
        payload.setFromDate(leave.getFromDate());
        payload.setToDate(leave.getToDate());
        payload.setRejectionReason(leave.getRemarks() != null ? leave.getRemarks() : null);

        try {
            communicationClient.sendLeaveRejectedEmail(payload);
            log.info("Communication service is called to send rejected leave email");

            NotificationPayload applicantPayload = new NotificationPayload(
                    leave.getApplicantEmployeeId(),
                    "Leave Status",
                    "Sorry your leave is DENIED by "+authorityResponse.getData().getEmployeeName(),
                    "INFO"
            );
            communicationClient.sendPrivateNotification(applicantPayload);


        } catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";
            int extractedErrorCode = -100; // Defaults to MICROSERVICE_CALL_FAILED code

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);

                // Navigate inside the nested "response" block
                if (errorNode.has("response")) {
                    JsonNode responseNode = errorNode.get("response");
                    if (responseNode.has("message")) {
                        cleanErrorMessage = responseNode.get("message").asText();
                    }
                    if (responseNode.has("code")) {
                        extractedErrorCode = responseNode.get("code").asInt();
                    }
                } else if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            int httpStatusValue = (e.status() > 0) ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();

            // Map the integer code to the correct Enum instance safely
            CustomStatus status = CustomStatus.fromCode(extractedErrorCode);

            // Pass the clean extracted message to CustomException
            throw new CustomException(cleanErrorMessage, status, httpStatusValue);
        }

        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> getMyAppliedLeaves(String empId) {

        Long employeeId = Long.parseLong(empId);

        LocalDate startOfYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());

        log.info("Fetching all leave for employee Id {} from {}  to {}",employeeId, startOfYear, endOfYear);

        List<Leave> leaveList = leaveRepository.findByApplicantEmployeeIdAndDateRange(employeeId, startOfYear, endOfYear)
                .orElseThrow(() -> new CustomException(null, CustomStatus.NO_APPLIED_LEAVE_RECORDS_FOUND, 200));

        List<MyLeaveResponse> myLeaveResponseList = leaveList.stream()
                .map(leave ->{
                    String employeeName = getEmployeeNameByEmpId(leave.getApplicantEmployeeId());
                    String approverName = getEmployeeNameByEmpId(leave.getApproverEmpId());
                    String approvedByName = getEmployeeNameByEmpId(leave.getApprovedBy());
                    MyLeaveResponse myLeaveResponse = modelMapper.map(leave, MyLeaveResponse.class);
                    myLeaveResponse.setEmployeeName(employeeName);
                    myLeaveResponse.setLeaveId(leave.getId());
                    myLeaveResponse.setNumberOfLeavesApplied(leave.getWantedLeaves());
                    myLeaveResponse.setApproverName(approverName);
                    myLeaveResponse.setApproverEmployeeId(leave.getApproverEmpId());
                    myLeaveResponse.setApprovedBy(approvedByName);
                    myLeaveResponse.setApprovedByEmployeeId(leave.getApprovedBy());
                    myLeaveResponse.setRemarks(leave.getRemarks());
                    myLeaveResponse.setPendingLeaves(getRemainingLeavesByEmployeeId(leave.getApplicantEmployeeId()));

                    return myLeaveResponse;
                })
                .toList();

        return new SingleResponse<>(
                myLeaveResponseList,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> cancelMyLeave(CancelMyLeaveRequest request, String empId) {

        Long employeeId = Long.parseLong(empId);

        Leave leave = leaveRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.LEAVE_RECORDS_NOT_FOUND, 404));

        if (!leave.getApplicantEmployeeId().equals(employeeId)) {
            throw new CustomException(null, CustomStatus.UNAUTHORISED_ACCESS, 200);
        }

        if (leave.getLeaveStatus().equals(LeaveStatusEnum.APPROVED)
                && !leave.getFromDate().isAfter(LocalDate.now())) {
            throw new CustomException(null, CustomStatus.LEAVE_ALREADY_STARTED, 200);
        }

        if (leave.getLeaveStatus().equals(LeaveStatusEnum.APPROVED)) {
            AvailableLeaves balance = availableLeavesRepository.findByEmployeeId(employeeId)
                    .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_LEAVE_BALANCE_RECORD_NOT_FOUND, 404));

            balance.setRemainingLeaves(balance.getRemainingLeaves() + leave.getWantedLeaves());
            availableLeavesRepository.save(balance);
        }

        if (StringUtils.hasText(request.getReason())) {
            leave.setRemarks(request.getReason().trim());
        }

        leave.setLeaveStatus(LeaveStatusEnum.CANCEL);

        leaveRepository.save(leave);

        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> getAppliedLeavesForMe(String empId) {

        Long employeeId = Long.parseLong(empId);

        List<Leave> leaveList = leaveRepository.findByApproverEmpId((employeeId))
                .orElseThrow(() -> new CustomException(null, CustomStatus.LEAVE_RECORDS_NOT_FOUND, 200));

        List<LeaveResponse> leaveResponseList = leaveList.stream()
                .sorted(Comparator.comparing(Leave::getLeaveStatus, (status1, status2) -> {
                    if (status1 == LeaveStatusEnum.PENDING && status2 != LeaveStatusEnum.PENDING) return -1;
                    if (status1 != LeaveStatusEnum.PENDING && status2 == LeaveStatusEnum.PENDING) return 1;
                    return 0; // If statuses are the same or neither is PENDING, keep their relative position
                }).thenComparing(Leave::getAppliedOn, Comparator.nullsLast(Comparator.reverseOrder())))
//                .filter(leave -> leave.getLeaveStatus().equals(LeaveStatusEnum.PENDING)) // TODO REMOVE FILTER
                .map(leave -> {
                    String employeeName = getEmployeeNameByEmpId(leave.getApplicantEmployeeId());
                    String approverName = getEmployeeNameByEmpId(leave.getApproverEmpId());
                    String approvedByName = getEmployeeNameByEmpId(leave.getApprovedBy());
                    LeaveResponse leaveResponse = modelMapper.map(leave, LeaveResponse.class);
                    leaveResponse.setEmployeeName(employeeName);
                    leaveResponse.setLeaveId(leave.getId());
                    leaveResponse.setNumberOfLeavesApplied(leave.getWantedLeaves());
                    leaveResponse.setApproverName(approverName);
                    leaveResponse.setApproverEmployeeId(leave.getApproverEmpId());
                    leaveResponse.setApprovedBy(approvedByName);
                    leaveResponse.setApprovedByEmployeeId(leave.getApprovedBy());
                    leaveResponse.setRemarks(leave.getRemarks());
                    leaveResponse.setRemainingLeaves(getRemainingLeavesByEmployeeId(leave.getApplicantEmployeeId()));
                    return leaveResponse;
                })
                .toList();
        return new SingleResponse<>(
                leaveResponseList,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> getAllPendingLeaves() {
        List<Leave> leaveList = leaveRepository.findByLeaveStatus(LeaveStatusEnum.PENDING)
                .orElseThrow(() -> new CustomException(null, CustomStatus.LEAVE_RECORDS_NOT_FOUND, 200));

        List<LeaveResponse> leaveResponseList = leaveList.stream()
                .filter(leave -> leave.getLeaveStatus() == LeaveStatusEnum.PENDING)
                .sorted(Comparator.comparing(Leave::getFromDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(leave -> {
                    String employeeName = getEmployeeNameByEmpId(leave.getApplicantEmployeeId());
                    String approverName = getEmployeeNameByEmpId(leave.getApproverEmpId());
                    String approvedByName = getEmployeeNameByEmpId(leave.getApprovedBy());
                    LeaveResponse leaveResponse = modelMapper.map(leave, LeaveResponse.class);
                    leaveResponse.setEmployeeName(employeeName);
                    leaveResponse.setLeaveId(leave.getId());
                    leaveResponse.setNumberOfLeavesApplied(leave.getWantedLeaves());
                    leaveResponse.setApproverName(approverName);
                    leaveResponse.setApproverEmployeeId(leave.getApproverEmpId());
                    leaveResponse.setApprovedBy(approvedByName);
                    leaveResponse.setApprovedByEmployeeId(leave.getApprovedBy());
                    leaveResponse.setRemarks(leave.getRemarks());
                    leaveResponse.setRemainingLeaves(getRemainingLeavesByEmployeeId(leave.getApplicantEmployeeId()));
                    return leaveResponse;
                })
                .toList();

        return new SingleResponse<>(
                leaveResponseList,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> getAllProcessedLeaves(){
        List<LeaveStatusEnum> statuses = List.of(
                LeaveStatusEnum.APPROVED,
                LeaveStatusEnum.DENIED,
                LeaveStatusEnum.CANCEL
        );

        LocalDate today = LocalDate.now();
        List<Leave> leaveList = leaveRepository.findByLeaveStatusInAndToDateGreaterThanEqual(statuses, today);

        List<LeaveResponse> leaveResponseList = leaveList.stream()
                .filter(leave -> leave.getLeaveStatus() != LeaveStatusEnum.PENDING)
                .sorted(Comparator.comparing(Leave::getFromDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(leave -> {
                    String employeeName = getEmployeeNameByEmpId(leave.getApplicantEmployeeId());
                    String approverName = getEmployeeNameByEmpId(leave.getApproverEmpId());
                    String approvedByName = getEmployeeNameByEmpId(leave.getApprovedBy());
                    LeaveResponse leaveResponse = modelMapper.map(leave, LeaveResponse.class);
                    leaveResponse.setEmployeeName(employeeName);
                    leaveResponse.setLeaveId(leave.getId());
                    leaveResponse.setNumberOfLeavesApplied(leave.getWantedLeaves());
                    log.info("Approver name {}",approverName);
                    leaveResponse.setApproverName(approverName);
                    leaveResponse.setApproverEmployeeId(leave.getApproverEmpId());
                    log.info("Approved By Name {}", approvedByName);
                    leaveResponse.setApprovedBy(approvedByName);
                    leaveResponse.setApprovedByEmployeeId(leave.getApprovedBy());
                    leaveResponse.setRemarks(leave.getRemarks());
                    leaveResponse.setRemainingLeaves(getRemainingLeavesByEmployeeId(leave.getApplicantEmployeeId()));
                    return leaveResponse;
                })
                .toList();

        return new SingleResponse<>(
                leaveResponseList,
                CustomStatus.SUCCESS
        );
    }

    // HELPER Method to get Employee name
    private String getEmployeeNameByEmpId(Long employeeId){
        SingleResponse<?> employeeResponse = null;
        try{

            if (employeeId != null) {
                log.info("Calling Employee Profile Service for {} details", employeeId);
                employeeResponse = employeeClient.getEmployeeName(employeeId);
                log.info("Employee {} details got", employeeId);
            }


        }catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";
            int extractedErrorCode = -100; // Defaults to MICROSERVICE_CALL_FAILED code

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);

                // Navigate inside the nested "response" block
                if (errorNode.has("response")) {
                    JsonNode responseNode = errorNode.get("response");
                    if (responseNode.has("message")) {
                        cleanErrorMessage = responseNode.get("message").asText();
                    }
                    if (responseNode.has("code")) {
                        extractedErrorCode = responseNode.get("code").asInt();
                    }
                } else if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            int httpStatusValue = (e.status() > 0) ? e.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();

            // Map the integer code to the correct Enum instance safely
            CustomStatus status = CustomStatus.fromCode(extractedErrorCode);

            // Pass the clean extracted message to CustomException
            throw new CustomException(cleanErrorMessage, status, httpStatusValue);
        }

        if(employeeResponse != null && employeeResponse.getData() != null){
            return employeeResponse.getData().toString();
        }
        return null;
    }

    // HELPER Method to get remaining leaves of the employee
    private Integer getRemainingLeavesByEmployeeId(Long employeeId){
        AvailableLeaves availableLeaves = availableLeavesRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_ID_NOT_FOUND, 200));

        return availableLeaves.getRemainingLeaves();
    }
}
