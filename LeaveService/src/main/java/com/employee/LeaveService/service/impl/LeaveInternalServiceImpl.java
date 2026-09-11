package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.client.CommunicationClient;
import com.employee.LeaveService.client.EmployeeClient;
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
import com.employee.LeaveService.service.LeaveInternalService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class LeaveInternalServiceImpl implements LeaveInternalService {

    private final LeaveRepository leaveRepository;

    private final ModelMapper modelMapper;

    private final LeaveTypeRepository leaveTypeRepository;

    private final EmployeeClient employeeClient;

    private final AvailableLeavesRepository availableLeavesRepository;

    private final CommunicationClient communicationClient;

    private final ObjectMapper objectMapper;

    @Override
    public SingleResponse<List<ListOfLeaveResponse>> getAllAppliedLeaves() {

        List<Leave> leaves = leaveRepository.findByFromDateGreaterThanEqual(LocalDate.now());
        log.info("Fetching the applied leaves list from today onwards");

        List<ListOfLeaveResponse> leaveResponses = leaves.stream()
                .filter(leave -> leave.getLeaveStatus() != LeaveStatusEnum.CANCEL)
                .sorted(Comparator.comparing(Leave::getAppliedOn, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(l -> {
                    if(l.getApprovedBy() == null){
                         l.setApprovedBy(null);
                         String employeeName = getEmployeeNameByEmpId(l.getApplicantEmployeeId());
                        String approverName = getEmployeeNameByEmpId(l.getApproverEmpId());
                        String approvedByName = getEmployeeNameByEmpId(l.getApprovedBy());
                        ListOfLeaveResponse leaveResponse = modelMapper.map(l, ListOfLeaveResponse.class);
                        leaveResponse.setEmployeeName(employeeName);
                        leaveResponse.setLeaveId(l.getId());
                        leaveResponse.setNumberOfLeavesApplied(l.getWantedLeaves());
                        leaveResponse.setApproverName(approverName);
                        leaveResponse.setApproverEmployeeId(l.getApproverEmpId());
                        leaveResponse.setApprovedBy(approvedByName);
                        leaveResponse.setApprovedByEmployeeId(l.getApprovedBy());
                        leaveResponse.setRemarks(l.getRemarks());
                        leaveResponse.setRemainingLeaves(getRemainingLeavesByEmployeeId(l.getApplicantEmployeeId()));
                        AvailableLeaves availableLeaves = availableLeavesRepository.findByEmployeeId(l.getApplicantEmployeeId())
                                        .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_ID_NOT_FOUND, 200));
                        leaveResponse.setRemainingLeaves(availableLeaves.getRemainingLeaves());
                        return leaveResponse;
                    }else{
                        String approverName = getEmployeeNameByEmpId(l.getApproverEmpId());
                        String approvedByName = getEmployeeNameByEmpId(l.getApprovedBy());
                        ListOfLeaveResponse leaveResponse = modelMapper.map(l, ListOfLeaveResponse.class);
                        leaveResponse.setLeaveId(l.getId());
                        leaveResponse.setNumberOfLeavesApplied(l.getWantedLeaves());
                        leaveResponse.setApproverName(approverName);
                        leaveResponse.setApproverEmployeeId(l.getApproverEmpId());
                        leaveResponse.setApprovedBy(approvedByName);
                        leaveResponse.setApprovedByEmployeeId(l.getApprovedBy());
                        leaveResponse.setRemarks(l.getRemarks());
                        leaveResponse.setRemainingLeaves(getRemainingLeavesByEmployeeId(l.getApplicantEmployeeId()));
                        AvailableLeaves availableLeaves = availableLeavesRepository.findByEmployeeId(l.getApplicantEmployeeId())
                                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_ID_NOT_FOUND, 200));
                        leaveResponse.setRemainingLeaves(availableLeaves.getRemainingLeaves());
                        return leaveResponse;
                    }
                })
                .toList();
        log.info("Loading list of leaves to the payload for internal server communication");
        return new SingleResponse<>(
                leaveResponses,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<List<LeaveTypeResponse>> getLeaveTypeList() {
        List<LeaveType> leaveTypeList = leaveTypeRepository.findAll();

        List<LeaveTypeResponse> leaveTypeResponseList = leaveTypeList.stream()
                .sorted(Comparator.comparing(LeaveType::getLeaveType, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(leaveType -> modelMapper.map(leaveType, LeaveTypeResponse.class))
                .toList();
        return new SingleResponse<>(
                leaveTypeResponseList,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public boolean isEmployeeOnLeave(Long employeeId, LocalDate today) {
        return leaveRepository.isEmployeeOnLeaveOnDate(employeeId, today);
    }

    @Override
    public SingleResponse<?> approveLeave(ApproveLeaveRequest request, String approverId) {

//        Long approverEmpId = Long.parseLong(approverId);

        Leave leave = leaveRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.LEAVE_ID_NOT_FOUND,404));

        AvailableLeaves availableLeaves = availableLeavesRepository.findByEmployeeId(leave.getApplicantEmployeeId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_LEAVE_BALANCE_RECORD_NOT_FOUND, 200));

        Long authorityEmployeeId = Long.parseLong(approverId);

        if(leave.getApplicantEmployeeId().equals(authorityEmployeeId)){
            throw new CustomException(null, CustomStatus.UNAUTHORIZED_LEAVE_APPROVER, 200);
        }

        if(!leave.getLeaveStatus().equals(LeaveStatusEnum.PENDING)){
            throw new CustomException(null, CustomStatus.LEAVE_ALREADY_PROCESSED, 200);
        }

        SingleResponse<EmployeeResponse> empResponse;
        try{

            log.info("Calling Employee Profile Service for {} details", authorityEmployeeId);
            empResponse = employeeClient.getEmployeeByEmployeeId(authorityEmployeeId);
            log.info("Employee {} details got", authorityEmployeeId);

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
        if(!empResponse.getData().isCanApproveLeave() && !leave.getApproverEmpId().equals(authorityEmployeeId)) {
            throw new CustomException(null, CustomStatus.UNAUTHORIZED_LEAVE_APPROVER, 200);
        }

        if(leave.getApprovedBy() == null){
            leave.setApprovedBy(empResponse.getData().getId());
            leave.setLeaveStatus(LeaveStatusEnum.APPROVED);
            if(StringUtils.hasText(request.getRemarks()) ){
                leave.setRemarks(request.getRemarks().trim());
            }
            int updatedBalance = availableLeaves.getRemainingLeaves() - leave.getWantedLeaves();
            availableLeaves.setEmployeeId(leave.getApplicantEmployeeId());
            availableLeaves.setRemainingLeaves(updatedBalance);
            availableLeavesRepository.save(availableLeaves);
            leaveRepository.save(leave);
        }

        NotificationPayload applicantPayload = new NotificationPayload(
                leave.getApplicantEmployeeId(),
                "Leave Status",
                "The leave you applied on from "+leave.getFromDate()+" to "+leave.getToDate()+" for "+leave.getWantedLeaves()+"day(s) is APPROVED By "+empResponse.getData().getEmployeeName(),
                "INFO"
        );
        communicationClient.sendPrivateNotification(applicantPayload);



        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> rejectLeave(RejectLeaveRequest request, String authorityId) {
        Long authorityEmployeeId = Long.parseLong(authorityId);
        Leave leave = leaveRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.LEAVE_ID_NOT_FOUND, 404));

        if(leave.getApplicantEmployeeId().equals(authorityEmployeeId)){
            throw new CustomException(null, CustomStatus.UNAUTHORIZED_LEAVE_APPROVER, 200);
        }

        if(!leave.getLeaveStatus().equals(LeaveStatusEnum.PENDING)){
            throw new CustomException(null, CustomStatus.LEAVE_ALREADY_PROCESSED, 200);
        }

        SingleResponse<EmployeeResponse> empResponse;
        try{

            log.info("Calling Employee Profile Service for {} details", authorityEmployeeId);
            empResponse = employeeClient.getEmployeeByEmployeeId(authorityEmployeeId);
            log.info("Employee {} details got", authorityEmployeeId);

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
        if(!empResponse.getData().isCanApproveLeave() && !leave.getApproverEmpId().equals(authorityEmployeeId)) {
            throw new CustomException(null, CustomStatus.UNAUTHORIZED_LEAVE_APPROVER, 200);
        }

        if(leave.getApprovedBy() == null){
            leave.setApprovedBy(empResponse.getData().getId());
            leave.setLeaveStatus(LeaveStatusEnum.DENIED);
            if(StringUtils.hasText(request.getRejectionReason())){
                leave.setRemarks(request.getRejectionReason().trim());
            }

            leaveRepository.save(leave);
        }

        NotificationPayload applicantPayload = new NotificationPayload(
                leave.getApplicantEmployeeId(),
                "Leave Status",
                "Sorry your leave is DENIED by "+empResponse.getData().getEmployeeName(),
                "INFO"
        );
        communicationClient.sendPrivateNotification(applicantPayload);

        return new SingleResponse<>(
                null,
               CustomStatus.SUCCESS
        );
    }

//    @Override
//    public ApiResponse<?> updateLeave(UpdateLeaveRequest request, String approvedEmployeeId) {
//        Leave leave = leaveRepository.findById(request.getLeaveId())
//                .orElseThrow(() -> new CustomException("Leave Id not found", HttpStatus.NOT_FOUND));
//
//        ApiResponse<EmployeeResponse> empResponse;
//        try{
//
//            log.info("Calling Employee Profile Service for {} details",approvedEmployeeId);
//            empResponse = employeeClient.getEmployeeByEmployeeId(approvedEmployeeId);
//            log.info("Employee {} details got", approvedEmployeeId);
//
//        }catch (FeignException feignException) {
//            String cleanErrorMessage = "Microservice called failed";
//            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//            if (feignException.status() > 0) {
//                try {
//                    responseStatus = HttpStatus.valueOf(feignException.status());
//                } catch (IllegalArgumentException ex) {
//                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//                }
//            } else {
//                cleanErrorMessage = "Service is unreachable. Please try again later.";
//                responseStatus = HttpStatus.SERVICE_UNAVAILABLE;
//            }
//            log.error("Employee Service unreachable");
//            throw new CustomException(cleanErrorMessage, responseStatus);
//        }
//
////        if(){
////         // TODO The higher authority can't approve their leave by themselves
////        }
//        if(!empResponse.getData().isCanApproveLeave()) {
//            throw new CustomException("Only higher authorities can approve leave!", HttpStatus.UNAUTHORIZED);
//        }
//
//        if(leave.getApprovedBy() == null || leave.getApprovedBy().isEmpty()){
//            leave.setApprovedBy(empResponse.getData().getEmployeeId());
//            leave.setLeaveStatus(request.getLeaveStatus());
//            leaveRepository.save(leave);
//        }
//        return new ApiResponse<>(
//                true,
//                "Leave approved",
//                null,
//                LocalDateTime.now(),
//                200
//        );
//    }

    @Override
    public SingleResponse<Set<LocalDate>> getEmployeeLeaveDatesInRange(Long empId, LocalDate startDate, LocalDate endDate) {
        log.info("Request received to extract leaves for employeeId: {} between {} and {}", empId, startDate, endDate);

        List<Leave> leaves = leaveRepository.findApprovedLeavesInDateRange(empId, startDate, endDate);
        log.info("Found {} approved leave record(s) in DB for employeeId: {}", leaves.size(), empId);
        // Expand date ranges into a Set of individual dates
        Set<LocalDate> leaveDates = leaves.stream()
                .flatMap(leave -> {
                    // Find the intersection points to avoid processing dates outside the requested window
                    LocalDate actualStart = leave.getFromDate().isBefore(startDate) ? startDate : leave.getFromDate();
                    LocalDate actualEnd = leave.getToDate().isAfter(endDate) ? endDate : leave.getToDate();

                    log.debug("Processing leave record ID: {}. Original: [{} to {}] -> Clipped Window: [{} to {}]",
                            leave.getId(), leave.getFromDate(), leave.getToDate(), actualStart, actualEnd);

                    // datesUntil is exclusive of the end date, so we add 1 day to make it inclusive
                    return actualStart.datesUntil(actualEnd.plusDays(1));
                })
                .collect(Collectors.toSet());
        log.info("Successfully expanded leave records into {} individual leave date(s) for employeeId: {}. Dates: {}",leaveDates.size(), empId, leaveDates);
    return new SingleResponse<>(
                leaveDates,
            CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<AvailableLeaves> saveAvailableLeaves(AvailableLeavesPayload payload) {
        AvailableLeaves savePayload=modelMapper.map(payload,AvailableLeaves.class);
        savePayload.setId(null);
        return new SingleResponse<>(
                availableLeavesRepository.save(savePayload)
                ,CustomStatus.SUCCESS);
    }



    @Override
    public SingleResponse<AvailableLeaves> getAvailableLeavesById(Long id) {
    AvailableLeaves response = availableLeavesRepository.findById(id).orElseThrow(()->
            new CustomException(null, CustomStatus.AVAILABLE_LEAVE_NOT_FOUND,404)
            );

        return new SingleResponse<>(
                response
                ,CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<?> deleteAvailableLeaves(Long id) {

         availableLeavesRepository.findById(id).orElseThrow(()->
                new CustomException(null, CustomStatus.AVAILABLE_LEAVE_NOT_FOUND,404)
        );
         try {
             availableLeavesRepository.deleteById(id);
             return new SingleResponse<>(
                     null
                     , CustomStatus.SUCCESS);
         } catch (Exception e) {
             throw new CustomException(null,CustomStatus.AVAILABLE_LEAVE_ERROR_DELETING,409);
         }

    }
    // HELPER Method to get Employee name
    private String getEmployeeNameByEmpId(Long employeeId){
        SingleResponse<?> employeeApiResponse = null;
        try{

            if (employeeId != null) {
                log.info("Calling Employee Profile Service for {} details", employeeId);
                employeeApiResponse = employeeClient.getEmployeeName(employeeId);
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

        if(employeeApiResponse != null && employeeApiResponse.getData() != null){
            return employeeApiResponse.getData().toString();
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
