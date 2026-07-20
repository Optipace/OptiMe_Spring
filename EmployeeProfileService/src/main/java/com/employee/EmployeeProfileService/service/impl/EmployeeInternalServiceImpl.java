package com.employee.EmployeeProfileService.service.impl;

import com.employee.EmployeeProfileService.client.AdminClient;
import com.employee.EmployeeProfileService.dto.request.*;
import com.employee.EmployeeProfileService.dto.response.*;
import com.employee.EmployeeProfileService.enums.AccountStatus;
import com.employee.EmployeeProfileService.enums.RoleEnum;
import com.employee.EmployeeProfileService.exception.CustomException;
import com.employee.EmployeeProfileService.model.*;
import com.employee.EmployeeProfileService.repository.*;
import com.employee.EmployeeProfileService.service.EmployeeInternalService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public ApiResponse<?> createProfile(EmployeeProfileRequest request) {

        EmployeeDesignation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new CustomException("No such designation found", HttpStatus.NOT_FOUND));

        WorkType workType = workTypeRepository.findById(request.getWorkTypeId())
                .orElseThrow(() -> new CustomException("No such work type found", HttpStatus.NOT_FOUND));

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
        EmployeeStatus status = employeeStatusRepository.findById(5L)
                        .orElseThrow(() -> new CustomException("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR));
        newEmployee.setStatus(status);

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
        employee.setAccountStatus(AccountStatus.ACTIVE);
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
        try {

            log.info("Calling Admin service for office response");
            ApiResponse<OfficeResponse> officeApiResponse = adminClient.getOfficeDetails(employee.getOfficeId());
            log.info("Received response from Admin service");

            if(officeApiResponse.getData() != null){
                response.setOffice(officeApiResponse.getData());
            }else{
                response.setOffice(null);
            }

        } catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice called failed";

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asString();
                } else {
                    cleanErrorMessage = rawErrorJson;
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }
            // Resolve status code safely.
            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e.status() > 0) {
                try {
                    responseStatus = HttpStatus.valueOf(e.status());
                    System.out.println(responseStatus);
                } catch (IllegalArgumentException ex) {
                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            } else {
                cleanErrorMessage = "Service is unreachable. Please try again later.";
                responseStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503 Status
            }
            throw new CustomException(cleanErrorMessage, responseStatus);
        }
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

        List<EmployeeDesignation> employeeDesignationList = designationRepository.findAll();
        List<RoleEnum> roleEnumList = List.of(RoleEnum.values());
        List<WorkType> workTypeList = workTypeRepository.findAll();
        List<EmployeeStatus> employeeStatusList = employeeStatusRepository.findAll();

        List<EmployeeDesignationResponse> employeeDesignationResponseList = employeeDesignationList.stream()
                .map(designation -> modelMapper.map(designation, EmployeeDesignationResponse.class))
                .toList();

        List<WorkTypeResponse> workTypeResponseList = workTypeList.stream()
                .map(workType -> modelMapper.map(workType, WorkTypeResponse.class))
                .toList();

        List<EmployeeStatusResponse> employeeStatusResponseList = employeeStatusList.stream()
                .map(status -> modelMapper.map(status, EmployeeStatusResponse.class))
                .toList();

        MasterEmployeeResponse masterEmployeeResponse = new MasterEmployeeResponse(employeeDesignationResponseList, roleEnumList, workTypeResponseList, employeeStatusResponseList);

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
        return employeeRepository.existsByEmployeeId(employeeId);
    }

    @Override
    public ApiResponse<?> updateEmployeeStatus(UpdateEmployeeStatusRequest request) {
        Employee employee = employeeRepository.findEmployeeByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new CustomException("Employee-Id not found", HttpStatus.NOT_FOUND));

        employee.setAccountStatus(request.getAccountStatus());
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
        response.setEmployeeDesignation(employee.getDesignation().getDesignation());
        response.setEmployeeStatus(employee.getStatus().getStatus());
        String designation = employee.getDesignation().getDesignation().toUpperCase();
        boolean canApproveLeave = designation.contains("MANAGER") || designation.contains("HR") ||
                designation.contains("PROJECT_MANAGER") || designation.contains("TEAM LEADER") || designation.contains("CEO") ||
                designation.contains("CTO");
        response.setCanApproveLeave(canApproveLeave);

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

    @Override
    public ApiResponse<List<FeedbackResponse>> getFeedback() {
        List<Feedback> feedbackList = feedbackRepository.findAll();

        List<FeedbackResponse> feedbackResponses = feedbackList.stream()
                .map(f -> modelMapper.map(f, FeedbackResponse.class))
                .toList();
        return new ApiResponse<>(
                true,
                "Feedback List",
                feedbackResponses,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<?> updateFeedback(FeedbackUpdateRequest request) {
        Feedback feedback = feedbackRepository.findById(request.getFeedbackId())
                .orElseThrow(() -> new CustomException("Respected Feedback Id not found", HttpStatus.NOT_FOUND));

        feedback.setStatusEnum(request.getFeedbackStatus());
        feedbackRepository.save(feedback);
        return new ApiResponse<>(
                true,
                "Feedback Updated",
                null,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public boolean getWorkTypeId(Long workTypeId) {
        return workTypeRepository.existsById(workTypeId);
    }

}
