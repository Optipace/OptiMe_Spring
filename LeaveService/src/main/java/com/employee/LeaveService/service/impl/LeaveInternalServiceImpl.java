package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.client.CommunicationClient;
import com.employee.LeaveService.client.EmployeeClient;
import com.employee.LeaveService.dto.request.ApproveLeaveRequest;
import com.employee.LeaveService.dto.request.NotificationPayload;
import com.employee.LeaveService.dto.request.RejectLeaveRequest;
import com.employee.LeaveService.dto.request.UpdateLeaveRequest;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Override
    public ApiResponse<?> getAllAppliedLeaves() {

        List<Leave> leaves = leaveRepository.findByFromDateGreaterThanEqual(LocalDate.now());
        log.info("Fetching the applied leaves list from today onwards");

        List<ListOfLeaveResponse> leaveResponses = leaves.stream()
                .filter(leave -> leave.getLeaveStatus() != LeaveStatusEnum.CANCEL)
                .map(l -> {
                    if(l.getApprovedBy() == null){
                         l.setApprovedBy(null);
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
        return new ApiResponse<>(
                true,
                "List of Leaves",
                leaveResponses,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<List<LeaveTypeResponse>> getLeaveTypeList() {
        List<LeaveType> leaveTypeList = leaveTypeRepository.findAll();

        List<LeaveTypeResponse> leaveTypeResponseList = leaveTypeList.stream()
                .map(leaveType -> modelMapper.map(leaveType, LeaveTypeResponse.class))
                .toList();
        return new ApiResponse<>(
                true,
                "List of Leave types",
                leaveTypeResponseList,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public boolean isEmployeeOnLeave(String employeeId, LocalDate today) {
        return leaveRepository.isEmployeeOnLeaveOnDate(employeeId, today);
    }

    @Override
    public ApiResponse<?> approveLeave(ApproveLeaveRequest request, String authorityEmployeeId) {
        Leave leave = leaveRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.LEAVE_ID_NOT_FOUND,404));

        AvailableLeaves availableLeaves = availableLeavesRepository.findByEmployeeId(leave.getApplicantEmployeeId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_LEAVE_BALANCE_RECORD_NOT_FOUND, 200));

        if(leave.getApplicantEmployeeId().equals(authorityEmployeeId)){
            throw new CustomException(null, CustomStatus.UNAUTHORIZED_LEAVE_APPROVER, 200);
        }

        if(!leave.getLeaveStatus().equals(LeaveStatusEnum.PENDING)){
            throw new CustomException(null, CustomStatus.LEAVE_ALREADY_PROCESSED, 200);
        }

        ApiResponse<EmployeeResponse> empResponse;
        try{

            log.info("Calling Employee Profile Service for {} details", authorityEmployeeId);
            empResponse = employeeClient.getEmployeeByEmployeeId(authorityEmployeeId);
            log.info("Employee {} details got", authorityEmployeeId);

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
            throw new CustomException(cleanErrorMessage, CustomStatus.SERVICE_UNAVAILABLE ,responseStatus.value());
        }

//        if(){
//         // TODO The higher authority can't approve their leave by themselves
//        }
        if(!empResponse.getData().isCanApproveLeave() && !leave.getApproverEmpId().equals(authorityEmployeeId)) {
            throw new CustomException(null, CustomStatus.UNAUTHORIZED_LEAVE_APPROVER, 200);
        }

        if(leave.getApprovedBy() == null || leave.getApprovedBy().isEmpty()){
            leave.setApprovedBy(empResponse.getData().getEmployeeId());
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



        return new ApiResponse<>(
                true,
                "Leave Approved",
                null,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<?> rejectLeave(RejectLeaveRequest request, String authorityEmployeeId) {
        Leave leave = leaveRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.LEAVE_ID_NOT_FOUND, 404));

        if(leave.getApplicantEmployeeId().equals(authorityEmployeeId)){
            throw new CustomException(null, CustomStatus.UNAUTHORIZED_LEAVE_APPROVER, 200);
        }

        if(!leave.getLeaveStatus().equals(LeaveStatusEnum.PENDING)){
            throw new CustomException(null, CustomStatus.LEAVE_ALREADY_PROCESSED, 200);
        }

        ApiResponse<EmployeeResponse> empResponse;
        try{

            log.info("Calling Employee Profile Service for {} details", authorityEmployeeId);
            empResponse = employeeClient.getEmployeeByEmployeeId(authorityEmployeeId);
            log.info("Employee {} details got", authorityEmployeeId);

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
            throw new CustomException(cleanErrorMessage, CustomStatus.SERVICE_UNAVAILABLE ,responseStatus.value());
        }

//        if(){
//         // TODO The higher authority can't approve their leave by themselves
//        }
        if(!empResponse.getData().isCanApproveLeave() && !leave.getApproverEmpId().equals(authorityEmployeeId)) {
            throw new CustomException(null, CustomStatus.UNAUTHORIZED_LEAVE_APPROVER, 200);
        }

        if(leave.getApprovedBy() == null || leave.getApprovedBy().isEmpty()){
            leave.setApprovedBy(empResponse.getData().getEmployeeId());
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

        return new ApiResponse<>(
                true,
                "Leave Rejected",
                null,
                LocalDateTime.now(),
                200
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
    public ApiResponse<Set<LocalDate>> getEmployeeLeaveDatesInRange(String employeeId, LocalDate startDate, LocalDate endDate) {
        log.info("Request received to extract leaves for employeeId: {} between {} and {}", employeeId, startDate, endDate);

        List<Leave> leaves = leaveRepository.findApprovedLeavesInDateRange(employeeId, startDate, endDate);
        log.info("Found {} approved leave record(s) in DB for employeeId: {}", leaves.size(), employeeId);
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
        log.info("Successfully expanded leave records into {} individual leave date(s) for employeeId: {}. Dates: {}",leaveDates.size(), employeeId, leaveDates);
    return new ApiResponse<>(
                true,
                "List of leave Dates",
                leaveDates,
                LocalDateTime.now(),
                200
        );
    }

    // HELPER Method to get Employee name
    private String getEmployeeNameByEmpId(String employeeId){
        ApiResponse<?> employeeResponse = null;
        try{

            if (employeeId != null) {
                log.info("Calling Employee Profile Service for {} details", employeeId);
                employeeResponse = employeeClient.getEmployeeName(employeeId);
                log.info("Employee {} details got", employeeId);
            }


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
            throw new CustomException(cleanErrorMessage, CustomStatus.SERVICE_UNAVAILABLE ,responseStatus.value());
        }

        if(employeeResponse != null && employeeResponse.getData() != null){
            return employeeResponse.getData().toString();
        }
        return null;
    }

    // HELPER Method to get remaining leaves of the employee
    private Integer getRemainingLeavesByEmployeeId(String employeeId){
        AvailableLeaves availableLeaves = availableLeavesRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_ID_NOT_FOUND, 200));

        return availableLeaves.getRemainingLeaves();
    }
}
