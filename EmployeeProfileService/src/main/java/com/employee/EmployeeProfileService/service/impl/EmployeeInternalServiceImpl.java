package com.employee.EmployeeProfileService.service.impl;

import com.employee.EmployeeProfileService.client.AdminClient;
import com.employee.EmployeeProfileService.client.AttendanceClient;
import com.employee.EmployeeProfileService.dto.request.*;
import com.employee.EmployeeProfileService.dto.response.*;
import com.employee.EmployeeProfileService.enums.AccountStatus;
import com.employee.EmployeeProfileService.enums.CustomStatus;
import com.employee.EmployeeProfileService.enums.RoleEnum;
import com.employee.EmployeeProfileService.exception.CustomException;
import com.employee.EmployeeProfileService.model.*;
import com.employee.EmployeeProfileService.repository.*;
import com.employee.EmployeeProfileService.service.EmployeeInternalService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class EmployeeInternalServiceImpl implements EmployeeInternalService {

    private final EmployeeRepository employeeRepository;

    private final ModelMapper modelMapper;

    private final FeedbackRepository feedbackRepository;

    private final EmployeeDesignationRepository designationRepository;

    private final WorkTypeRepository workTypeRepository;

    private final EmployeeStatusRepository employeeStatusRepository;

    private final AdminClient adminClient;

    private final ObjectMapper objectMapper;

    private final AttendanceClient attendanceClient;

    @Override
    public SingleResponse<?> createProfile(EmployeeProfileRequest request) {

        EmployeeDesignation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new CustomException("No such designation found", CustomStatus.DESIGNATION_NOT_FOUND, 404));

        WorkType workType = workTypeRepository.findById(request.getWorkTypeId())
                .orElseThrow(() -> new CustomException("No such work type found", CustomStatus.INVALID_WORK_TYPE, 404));

        Employee newEmployee =  new Employee();
        newEmployee.setEmployeeId(request.getEmployeeId());
        newEmployee.setEmployeeName(request.getEmployeeName());
        newEmployee.setContact(request.getContact());
        newEmployee.setEmailId(request.getEmailId());
        newEmployee.setDesignation(designation);

        if(String.valueOf(request.getRole()).equals("ADMIN")){
            newEmployee.setRole(request.getRole());
        }

        newEmployee.setRole(request.getRole());
        newEmployee.setGender(request.getGender());
        newEmployee.setWorkType(workType);
        newEmployee.setDateOfBirth(request.getDateOfBirth());
        newEmployee.setProfileStatus(4);
        newEmployee.setDateOfJoining(request.getDateOfJoining());
        newEmployee.setPermanentAddress(request.getPermanentAddress());
        newEmployee.setOfficeId(request.getOfficeId());
        newEmployee.setUserId(request.getUserId());
        EmployeeStatus status = employeeStatusRepository.findByStatus("NEW JOINEE")
                        .orElseThrow(() -> new CustomException("Failed to update status", CustomStatus.EMPLOYEE_STATUS_UPDATE_FAILED, 409));
        newEmployee.setStatus(status);

        employeeRepository.save(newEmployee);
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> completeProfile(CompleteProfileRequest request) {

        Employee employee = employeeRepository.findEmployeeByUserId(request.getUserId())
                .orElseThrow(() -> new CustomException("Employee not found",CustomStatus.EMPLOYEE_ID_NOT_FOUND, 404));

        int currentStatus = employee.getProfileStatus();

        if(currentStatus == 6 || currentStatus == 7){
            throw new CustomException("Profile already completed please login", CustomStatus.PROFILE_ALREADY_COMPLETED, 400);
        }
        employee.setCurrentAddress(request.getCurrentAddress());
        employee.setEmergencyContact(request.getEmergencyContact());
        employee.setBloodGroup(request.getBloodGroup());
//        employee.setProfileStatus(ProfileStatusEnum.COMPLETE);
        int result = currentStatus | 2;
        employee.setProfileStatus(result);
        employee.setAccountStatus(AccountStatus.ACTIVE);
        employeeRepository.save(employee);

        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<Long> getEmployeeByUserId(Long userId) {
        Employee employee = employeeRepository.findEmployeeByUserId(userId)
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_ID_NOT_FOUND, 404));

        return new SingleResponse<>(
                employee.getId(),
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<EmployeeGetProfileResponse> getProfile(String employeeId) {
        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException("Employee not found", CustomStatus.EMPLOYEE_ID_NOT_FOUND, 404));


        EmployeeGetProfileResponse response = modelMapper.map(employee,EmployeeGetProfileResponse.class);
        response.setWorkType(employee.getWorkType().getName());
        response.setId(employee.getId());
        response.setEmployeeId(employee.getEmployeeId());
        if(employee.getRole() != null){
            response.setRole(employee.getRole().toString());
        }

        try {

            log.info("Calling Admin service for office response");
            SingleResponse<OfficeResponse> officeApiResponse = adminClient.getOfficeDetails(employee.getOfficeId());
            log.info("Received response from Admin service");

            if(officeApiResponse.getData() != null){
                response.setOffice(officeApiResponse.getData());
            }else{
                response.setOffice(null);
            }

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
                response,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> getMasterDetails(){

        List<EmployeeDesignation> employeeDesignationList = designationRepository.findAll();
        List<RoleEnum> roleEnumList = List.of(RoleEnum.values());
        List<WorkType> workTypeList = workTypeRepository.findAll();
        List<EmployeeStatus> employeeStatusList = employeeStatusRepository.findAll();

        List<EmployeeDesignationResponse> employeeDesignationResponseList = employeeDesignationList.stream()
                .sorted(Comparator.comparing(EmployeeDesignation::getId))
                .map(designation -> modelMapper.map(designation, EmployeeDesignationResponse.class))
                .toList();

        List<WorkTypeResponse> workTypeResponseList = workTypeList.stream()
                .sorted(Comparator.comparing(WorkType::getId))
                .map(workType -> modelMapper.map(workType, WorkTypeResponse.class))
                .toList();

        List<EmployeeStatusResponse> employeeStatusResponseList = employeeStatusList.stream()
                .sorted(Comparator.comparing(EmployeeStatus::getId))
                .map(status -> modelMapper.map(status, EmployeeStatusResponse.class))
                .toList();

        MasterEmployeeResponse masterEmployeeResponse = new MasterEmployeeResponse(employeeDesignationResponseList, roleEnumList, workTypeResponseList, employeeStatusResponseList);

        return new SingleResponse<>(
                masterEmployeeResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public boolean checkEmployeeByEmployeeId(Long employeeId) {
        return employeeRepository.existsById(employeeId);
    }

    @Override
    public SingleResponse<?> updateEmployeeStatus(UpdateEmployeeStatusRequest request) {
        Employee employee = employeeRepository.findEmployeeByUserId(request.getEmployeeId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_ID_NOT_FOUND, 404));

        employee.setAccountStatus(request.getAccountStatus());
        employeeRepository.save(employee);
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<EmployeeInternalResponse> getEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_ID_NOT_FOUND, 404));

        EmployeeInternalResponse response = modelMapper.map(employee, EmployeeInternalResponse.class);
        response.setId(employee.getId());
        response.setEmployeeDesignation(employee.getDesignation().getDesignation());
        response.setEmployeeStatus(employee.getStatus().getStatus());
        response.setEmployeeId(employee.getEmployeeId());
//        String designation = employee.getDesignation().getDesignation().toUpperCase();
//        boolean canApproveLeave = designation.contains("MANAGER") || designation.contains("HR") ||
//                designation.contains("PROJECT_MANAGER") || designation.contains("TEAM LEADER") || designation.contains("CEO") ||
//                designation.contains("CTO");
        log.info("The employee role is {}", employee.getRole().toString());
        boolean canApproveLeave = employee.getRole().equals(RoleEnum.ADMIN);
        log.info("The employee with id {} can approve leave : {}",employee.getEmployeeId(), canApproveLeave);

        response.setCanApproveLeave(canApproveLeave);

        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> deleteIdentity(String employeeId) {
        employeeRepository.findEmployeeByEmployeeId(employeeId).ifPresent( employee -> {
            log.info("Rollback executed: Employee {} deleted.", employeeId);
            employeeRepository.delete(employee);
        });
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<List<FeedbackResponse>> getFeedback() {
        List<Feedback> feedbackList = feedbackRepository.findAll();

        List<FeedbackResponse> feedbackResponses = feedbackList.stream()
                .map(f -> modelMapper.map(f, FeedbackResponse.class))
                .toList();
        return new SingleResponse<>(
                feedbackResponses,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> updateFeedback(FeedbackUpdateRequest request) {
        Feedback feedback = feedbackRepository.findById(request.getFeedbackId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.FEEDBACK_NOT_FOUND, 404));

        feedback.setStatusEnum(request.getFeedbackStatus());
        feedbackRepository.save(feedback);
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public boolean getWorkTypeId(Long workTypeId) {
        return workTypeRepository.existsById(workTypeId);
    }

    @Override
    public SingleResponse<PageResponse<EmployeeResponse>> getAllEmployee(Pageable pageable) {
        Pageable sortedPageable = pageable.getSort().isSorted() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Order.asc("employeeName").nullsLast()));

        Page<Employee> employeePage = employeeRepository.findAll(sortedPageable);
        List<Employee> employeeList = employeePage.getContent();

        if(employeePage.isEmpty()){
            throw new CustomException("Employee Records not found", CustomStatus.EMPLOYEE_NOT_FOUND,404);
        }

//        List<String> employeeIds = employeeList.stream()
//                .map(Employee::getEmployeeId)
//                .toList();

        // TODO Make ONE bulk network call to fetch all statuses at once (Create an api in ATTENDANCE SERVICE for bulk api call)
//        log.info("Attendance service is calling bulk status for {} employees", employeeIds.size());
//        ApiResponse<Map<String, String>> bulkApiResponse = attendanceClient.getBulkAttendanceStatus(employeeIds);
//        Map<String, String> attendanceStatusMap = bulkApiResponse != null && bulkApiResponse.getData() != null
//                ? bulkApiResponse.getData()
//                : Collections.emptyMap();
//        log.info("Bulk attendance service call complete");

        List<EmployeeResponse> employeeResponseList = employeeList.stream()
                .map(employee -> {
                    log.info("Attendance service is calling for Id {} employee {}", employee.getId(), employee.getEmployeeId());
                    SingleResponse<String> apiResponse = attendanceClient.getAttendanceStatus(employee.getId()); // TODO Make ONE bulk network call to fetch all statuses at once
                    log.info("Attendance service called");

                    String attendanceStatus = apiResponse.getData();

                    // 1. Map using modelMapper first
                    EmployeeResponse response = modelMapper.map(employee, EmployeeResponse.class);

                    // 2. Set the designation ID on the mapped object
                    if (employee.getDesignation() != null) {
                        response.setDesignationId(employee.getDesignation().getId());
                    }

                    response.setAttendanceStatus(attendanceStatus);

                    return response;
                })
                .toList();

        PageResponse<EmployeeResponse> response = new PageResponse<>(
                employeeResponseList,
                employeePage.getNumber(),
                employeePage.getSize(),
                employeePage.getTotalElements(),
                employeePage.getTotalPages(),
                employeePage.isLast()
        );
        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<ListOfEmployeeIdResponse> getAllEmployeeId() {
        List<Long> employeeList = employeeRepository.findActiveEmployeeIds();
        ListOfEmployeeIdResponse employeeIdResponse = new ListOfEmployeeIdResponse();
        employeeIdResponse.setEmployeeIds(employeeList);
        return new SingleResponse<>(
                employeeIdResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public boolean isHrEmployeeId(Long hrEmpId) {
        Employee employee = employeeRepository.findById(hrEmpId)
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_ID_NOT_FOUND, 404));

        if (employee.getDesignation() == null || employee.getDesignation().getDesignation() == null) {
            log.warn("The employee ID {} has no active designation assigned.", hrEmpId);
            return false;
        }

        boolean isHr = employee.getDesignation().getDesignation().toUpperCase().contains("HR");

        if (!isHr) {
            log.info("Authorization Rejected: The employee ID {} is not an HR member.", hrEmpId);
            return false;
        }

        log.info("Authorization Approved: The employee ID {} is verified as HR.", hrEmpId);
        return true;
    }

    @Override
    public SingleResponse<PageResponse<ListOfAdminInternalResponse>> getAllAdminDetails(Pageable pageable) {
        Page<Employee> adminPage = employeeRepository.findByRole(RoleEnum.ADMIN,pageable);
        List<Employee> adminList = adminPage.getContent();

        if(adminPage.isEmpty()){
            throw new CustomException("Employee Records not found", CustomStatus.EMPLOYEE_NOT_FOUND, 404);
        }

        List<ListOfAdminInternalResponse> adminResponseList = adminList.stream()
                .map(admin -> {
                    ListOfAdminInternalResponse response = new ListOfAdminInternalResponse();
                    response.setEmployeeId(admin.getId());
                    response.setContact(admin.getContact());
                    response.setEmployeeName(admin.getEmployeeName());
                    response.setEmailId(admin.getEmailId());
                    response.setWorkTypeId(admin.getWorkType().getId());

                    if (admin.getDesignation() != null) {
                        response.setDesignationId(admin.getDesignation().getId());
                    }
                    if (admin.getOfficeId() != null) {
                        response.setOfficeId(admin.getOfficeId());
                    }

                    return response;
                })
                .toList();

        PageResponse<ListOfAdminInternalResponse> response = new PageResponse<>(
                adminResponseList,
                adminPage.getNumber(),
                adminPage.getSize(),
                adminPage.getTotalElements(),
                adminPage.getTotalPages(),
                adminPage.isLast()
        );
        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> getEmployeeName(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_ID_NOT_FOUND, 404));

        String empName = employee.getEmployeeName();
        return new SingleResponse<>(
                empName,
                CustomStatus.SUCCESS
        );
    }
}
