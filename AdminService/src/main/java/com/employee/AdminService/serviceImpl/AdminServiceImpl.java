package com.employee.AdminService.serviceImpl;

import com.employee.AdminService.client.*;
import com.employee.AdminService.dto.request.*;
import com.employee.AdminService.dto.response.*;
import com.employee.AdminService.enums.CustomStatus;
import com.employee.AdminService.enums.OfficeStatus;
import com.employee.AdminService.enums.RoleEnum;
import com.employee.AdminService.exception.CustomException;
import com.employee.AdminService.model.Office;
import com.employee.AdminService.repository.OfficeRepository;
import com.employee.AdminService.service.AdminService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final AuthClient authClient;

    private final EmployeeClient employeeClient;

    private final AttendanceClient attendanceClient;

    private final ObjectMapper objectMapper;

    private final CommunicationClient communicationClient;

    private final LeaveClient leaveClient;

    private final ModelMapper modelMapper;

    private final OfficeRepository officeRepository;

    @Override
    public SingleResponse<?> addNewUser(RegisterRequest request, String adminEmployeeId) {

        officeRepository.findById(request.getOfficeId())
                .orElseThrow(() -> new CustomException("Office Id not found", CustomStatus.OFFICE_NOT_FOUND, 409));

        // 1. Prepare Auth Payload (Security Data)
        AuthIdentityPayload authPayload = new AuthIdentityPayload(
                request.getEmployeeName(),
                request.getEmployeeId(),
                request.getEmailId(),
                request.getContact(),
                request.getRole(),
                adminEmployeeId // The logged-in admin who is making this request
        );

        // 2. Prepare Profile Payload (HR Data)
        EmployeeProfilePayload profilePayload = new EmployeeProfilePayload(
                request.getEmployeeId(),
                request.getEmployeeName(),
                request.getContact(),
                request.getEmailId(),
                request.getDesignationId(),
                request.getRole(),
                request.getGender(),
                request.getWorkTypeId(),
                request.getOfficeId(),
                request.getDateOfBirth(),
                request.getDateOfJoining(),
                request.getPermanentAddress()
        );
        boolean isAuthCreated = false;
        boolean isEmployeeCreated = false;

        try {
            // 3. Call Auth service via Feign
            authClient.createIdentity(authPayload);
            log.info("Auth Service is called");
            isAuthCreated = true;

            // 4. Call Employee Profile service via Feign
            employeeClient.createProfile(profilePayload);
            log.info("Employee Service is called");
            isEmployeeCreated = true;

            // 5. For email service
            communicationClient.sendAccountCreatedEmail(request.getEmailId());
            log.info("Triggered account created email");
            log.info("Communication service is called to send welcome email");

            // 6. Sending broadcast notification to ALL
            NotificationPayload payload = new NotificationPayload();
            payload.setEmployeeId("ALL");
            payload.setTitle("Company Announcement");
            payload.setMessage("Please welcome our new employee: " + request.getEmployeeName());
            payload.setType("INFO");

            communicationClient.sendBroadCastNotification(payload);
            log.info("Notification is broadcasted to everyone");
        } catch (FeignException e) {
            if (isAuthCreated) {
                try {
                    authClient.deleteIdentity(request.getEmployeeId());
                } catch (Exception ex) {
                    log.error("Rollback failed {} ", ex.getMessage());
                }
            }

            if (isEmployeeCreated) {
                try {
                    employeeClient.deleteIdentity(request.getEmployeeId());
                } catch (Exception ex) {
                    log.error("Rollback failed {}", ex.getMessage());
                }
            }
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
    public SingleResponse<PageResponse<EmployeeResponse>> getAllEmployee(Pageable pageable) {
        PageResponse<EmployeeResponse> pageData;
        try {
            // Call Employee Profile service via Feign
            ApiResponse<PageResponse<EmployeeResponse>> apiResponse = employeeClient.getAllEmployee(pageable);
            log.info("Employee Service is called");

            pageData = apiResponse.getData();
            List<EmployeeResponse> employeeResponseList = pageData.getContent();
            employeeResponseList.forEach(employee ->{
                if (employee.getOffice().getOfficeId() != null){
                    Office office = officeRepository.findById(employee.getOffice().getOfficeId())
                                    .orElse(null);
                    OfficeResponse officeResponse = new OfficeResponse(
                            office.getId(),
                            office.getOfficeName(),
                            office.getLatitude(),
                            office.getLongitude(),
                            office.getHrEmpId(),
                            office.getAddress(),
                            office.getContact(),
                            office.getGoogleMap()
                    );
                    employee.setOffice(officeResponse);
                }
                    });

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
                pageData,
                CustomStatus.SUCCESS
        );
    }


    @Override
    public SingleResponse<?> addNewOffice(AddNewOfficeRequest request) {
        if (officeRepository.existsById(request.getOfficeId())) {
            throw new CustomException(null, CustomStatus.OFFICE_ALREADY_EXISTS, 409);
        }

        Office newOffice = new Office();
        newOffice.setId(request.getOfficeId());
        newOffice.setOfficeName(request.getOfficeName());
        newOffice.setContact(request.getContact());
        newOffice.setAddress(request.getAddress());
        newOffice.setLatitude(request.getLatitude());
        newOffice.setLongitude(request.getLongitude());
        boolean isHrEmpId = false;
        try {
            isHrEmpId = employeeClient.isHrEmployeeId(request.getHrEmpId());
            log.info("Employee Service is called");

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

        if(isHrEmpId){
            newOffice.setHrEmpId(request.getHrEmpId());
        }else{
            throw new CustomException(null, CustomStatus.HR_EMP_ID_NOT_FOUND, 409);
        }
        newOffice.setGoogleMap(request.getGoogleMap());
        newOffice.setOfficeStatus(OfficeStatus.ACTIVE);

        officeRepository.save(newOffice);

        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }


    @Override
    public SingleResponse<PageResponse<OfficeResponse>> getOfficeList(Pageable pageable) {
        Pageable sortedPageable = pageable.getSort().isSorted() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.asc("officeName").nullsLast()));
        Page<Office> officePage = officeRepository.findAll(sortedPageable);
        List<Office> officeList = officePage.getContent();

        if(officePage.isEmpty()){
            throw new CustomException(null, CustomStatus.NO_OFFICE_RECORDS_FOUND, 409);
        }

        List<OfficeResponse> officeResponseList = officeList.stream()
                .map(office -> modelMapper.map(office, OfficeResponse.class))
                .toList();

        PageResponse<OfficeResponse> pageResponse = new PageResponse<>(
                officeResponseList,
                officePage.getNumber(),
                officePage.getSize(),
                officePage.getTotalElements(),
                officePage.getTotalPages(),
                officePage.isLast()
        );
        return new SingleResponse<>(
                pageResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> updateOffice(OfficeRequest request) {
        Office office = officeRepository.findById(request.getOfficeId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.NO_OFFICE_RECORDS_FOUND, 409));

        if(request.getOfficeName() != null){
            office.setOfficeName(request.getOfficeName());
        }

        if(request.getLatitude() != null){
            office.setLatitude(request.getLatitude());
        }

        if(request.getLongitude() != null){
            office.setLongitude(request.getLongitude());
        }

        if(request.getHrEmpId() != null){
            office.setHrEmpId(request.getHrEmpId());
        }

        if(request.getAddress() != null){
            office.setAddress(request.getAddress());
        }

        if(request.getContact() != null){
            office.setContact(request.getContact());
        }

        if(request.getGoogleMap() != null){
            office.setGoogleMap(request.getGoogleMap());
        }

        officeRepository.save(office);
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<PageResponse<String>> getOfficeNames(Pageable pageable) {
        Pageable sortedPageable = pageable.getSort().isSorted() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Order.asc("officeName").nullsLast()));
        Page<Office> officePage = officeRepository.findAll(sortedPageable);
        List<Office> officeList = officePage.getContent();

        List<String> officeNames = officeList.stream()
                .filter(office -> !OfficeStatus.INACTIVE.equals(office.getOfficeStatus()))
                .map(Office::getOfficeName)
                .toList();

        PageResponse<String> pageResponse = new PageResponse<>(
                officeNames,
                officePage.getNumber(),
                officePage.getSize(),
                officePage.getTotalElements(),
                officePage.getTotalPages(),
                officePage.isLast()
        );

        return new SingleResponse<>(
                pageResponse,
                CustomStatus.SUCCESS
        );
    }

    public SingleResponse<List<FeedbackResponse>> getFeedback(){
        List<FeedbackResponse> feedbackResponses = null;
        try{
            log.info("Calling employee service");
            ApiResponse<List<FeedbackResponse>> apiResponse = employeeClient.getFeedback();
            log.info("Employee service called for feedbacks list");

            if(apiResponse != null && apiResponse.getData() != null){
                feedbackResponses = apiResponse.getData();
            }
        }catch (FeignException fe){
            String rawErrorJson = fe.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";

            try{
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if(errorNode.has("message")){
                    cleanErrorMessage = errorNode.get("message").toString();
                }else{
                    cleanErrorMessage = rawErrorJson;
                }
            }catch (Exception e){
                cleanErrorMessage = rawErrorJson;
            }

            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if(fe.status() > 0){
                try{
                    responseStatus = HttpStatus.valueOf(fe.status());
                }catch (IllegalArgumentException ex){
                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            }else {
                cleanErrorMessage = "Service is unreachable. Please try again later.";
                responseStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503 Status
            }
            throw new CustomException(cleanErrorMessage, responseStatus);
        }
        return new SingleResponse<>(
                feedbackResponses,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> updateFeedback(FeedbackUpdateRequest request) {
        String message= "";
        try{
           ApiResponse<?> apiResponse = employeeClient.updateFeedback(request);
           message = apiResponse.getMessage();
        }catch (FeignException fe){
            String rawErrorJson = fe.contentUTF8();
            String cleanErrorMessage = "Microservices call failed";

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").toString();
                } else {
                    cleanErrorMessage = rawErrorJson;
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if (fe.status() > 0) {
                try {
                    responseStatus = HttpStatus.valueOf(fe.status());
                } catch (IllegalArgumentException ex) {
                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            } else {
                cleanErrorMessage = "Service is unreachable. Please try again later.";
                responseStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503 Status
            }
            throw new CustomException(cleanErrorMessage, responseStatus);
        }
        return new SingleResponse<>(
                message,
                CustomStatus.SUCCESS
        );
    }


    @Override
    public SingleResponse<?> getAllAppliedLeaves() {

        List<ListOfLeaveResponse> leaveResponses = new ArrayList<>();
        try {
            ApiResponse<List<ListOfLeaveResponse>> apiResponse = leaveClient.getAllAppliedLeaves();
            log.info("LEAVE SERVICE CALLED");
            if (apiResponse != null && apiResponse.getData() != null) {
                leaveResponses = apiResponse.getData().stream()
                        .map(l -> modelMapper.map(l, ListOfLeaveResponse.class))
                        .toList();
            }
        } catch (FeignException fe) {
            String rawErrorJson = fe.contentUTF8();
            String cleanErrorMessage = "Microservices call failed";

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").toString();
                } else {
                    cleanErrorMessage = rawErrorJson;
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if (fe.status() > 0) {
                try {
                    responseStatus = HttpStatus.valueOf(fe.status());
                } catch (IllegalArgumentException ex) {
                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            } else {
                cleanErrorMessage = "Service is unreachable. Please try again later.";
                responseStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503 Status
            }
            throw new CustomException(cleanErrorMessage, responseStatus);
        }
        return new SingleResponse<>(
                leaveResponses,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> sendBroadcastMessage(NotificationRequest request) {
        NotificationPayload payload = new NotificationPayload();
        payload.setEmployeeId("ALL");
        payload.setTitle(request.getTitle());
        payload.setMessage(request.getMessage());
        payload.setType(request.getType());

        communicationClient.sendBroadCastNotification(payload);

        return new SingleResponse<>(
                null,
               CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<MasterResponse> getMasterDetails() {
        List<Office> officeList = officeRepository.findAll();
        List<OfficeResponse> officeResponse = officeList.stream()
                .map(office -> modelMapper.map(office, OfficeResponse.class))
                .toList();
        ApiResponse<MasterEmployeeResponse> empResponse = employeeClient.getMasterDetails();
        ApiResponse<List<LeaveTypeResponse>> leaveResponse = leaveClient.getLeaveTypeList();

        List<EmployeeDesignationResponse> employeeDesignationResponseList = new ArrayList<>();
        List<RoleEnum> roleEnumList = new ArrayList<>();
        List<WorkTypeResponse> workTypeList = new ArrayList<>();
        List<EmployeeStatusResponse> employeeStatusList = new ArrayList<>();
        if(empResponse != null && empResponse.getData() != null){
            employeeDesignationResponseList = empResponse.getData().getAvailableDesignationsList();
            roleEnumList = empResponse.getData().getRoleEnumList();
            workTypeList = empResponse.getData().getWorkTypeList();
            employeeStatusList = empResponse.getData().getEmployeeStatusList();
        }else{
            throw new CustomException(null, CustomStatus.MICROSERVICE_CALL_FAILED, 500);
        }

        MasterResponse masterResponse = new MasterResponse();

        if(leaveResponse != null && leaveResponse.getData() != null){
            masterResponse.setLeaveTypeResponseList(leaveResponse.getData());
        }

        masterResponse.setOfficeResponse(officeResponse);
        masterResponse.setAvailableDesignations(employeeDesignationResponseList);
        masterResponse.setWorkTypeList(workTypeList);
        masterResponse.setRoleEnumList(roleEnumList);
        masterResponse.setEmployeeStatusList(employeeStatusList);

        return new SingleResponse<>(
                masterResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> updateOfficeStatus(UpdateOfficeStatusRequest request) {
        Office office = officeRepository.findById(request.getOfficeId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.OFFICE_NOT_FOUND, 409));

        office.setOfficeStatus(OfficeStatus.valueOf(request.getStatus()));
        officeRepository.save(office);
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> getTodayAttendanceRecords() {
        List<EmployeeAttendanceResponse> responses;
        try {
            ApiResponse<List<EmployeeAttendanceResponse>> apiResponse = attendanceClient.getTodayAttendanceRecords();
            responses = apiResponse.getData();
        } catch (FeignException fe) {
            String rawErrorJson = fe.contentUTF8();
            String cleanErrorMessage = "Microservices call failed";

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").toString();
                } else {
                    cleanErrorMessage = rawErrorJson;
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if (fe.status() > 0) {
                try {
                    responseStatus = HttpStatus.valueOf(fe.status());
                } catch (IllegalArgumentException ex) {
                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            } else {
                cleanErrorMessage = "Service is unreachable. Please try again later.";
                responseStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503 Status
            }
            throw new CustomException(cleanErrorMessage, responseStatus);
        }
        return new SingleResponse<>(
                responses,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> approveLeave(ApproveLeaveRequest request, String approvedEmployeeId) {
        ApiResponse<?> apiResponse;
        try {
            apiResponse = leaveClient.approveLeave(request, approvedEmployeeId);
        } catch (FeignException fe) {
            String rawErrorJson = fe.contentUTF8();
            String cleanErrorMessage = "Microservices call failed";

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").toString();
                } else {
                    cleanErrorMessage = rawErrorJson;
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if (fe.status() > 0) {
                try {
                    responseStatus = HttpStatus.valueOf(fe.status());
                } catch (IllegalArgumentException ex) {
                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            } else {
                cleanErrorMessage = "Service is unreachable. Please try again later.";
                responseStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503 Status
            }
            throw new CustomException(cleanErrorMessage, responseStatus);
        }
        return new SingleResponse<>(
               null, // TODO : Send error message if recieved
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> rejectLeave(RejectLeaveRequest request, String approvedEmployeeId) {
        ApiResponse<?> apiResponse;
        try {
            apiResponse = leaveClient.rejectLeave(request, approvedEmployeeId);
        } catch (FeignException fe) {
            String rawErrorJson = fe.contentUTF8();
            String cleanErrorMessage = "Microservices call failed";

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").toString();
                } else {
                    cleanErrorMessage = rawErrorJson;
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }

            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if (fe.status() > 0) {
                try {
                    responseStatus = HttpStatus.valueOf(fe.status());
                } catch (IllegalArgumentException ex) {
                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            } else {
                cleanErrorMessage = "Service is unreachable. Please try again later.";
                responseStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503 Status
            }
            throw new CustomException(cleanErrorMessage, responseStatus);
        }
        return new SingleResponse<>(
                null, // TODO : Send error message if recieved
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<PageResponse<AdminResponse>> getAllAdmin(Pageable pageable) {
        try {
            // 1. Call Employee Profile microservice via Feign client
            ApiResponse<PageResponse<ListOfAdminResponse>> apiResponse = employeeClient.getAllAdmin(pageable);
            log.info("Employee Service call completed successfully");

            PageResponse<ListOfAdminResponse> rawPageData = apiResponse.getData();
            if (rawPageData == null || rawPageData.getContent() == null) {
                return new SingleResponse<>(new PageResponse<>(), CustomStatus.SUCCESS);
            }

            // 2. Stream through ListOfAdminResponse and transform each into AdminResponse
            List<AdminResponse> transformedContent = rawPageData.getContent().stream()
                    .map(listOfAdmin -> {
                        AdminResponse adminResponse = new AdminResponse();
                        adminResponse.setEmployeeId(listOfAdmin.getEmployeeId());
                        adminResponse.setEmployeeName(listOfAdmin.getEmployeeName());
                        adminResponse.setContact(listOfAdmin.getContact());
                        adminResponse.setEmailId(listOfAdmin.getEmailId());
                        adminResponse.setDesignationId(listOfAdmin.getDesignationId());
                        adminResponse.setWorkTypeId(listOfAdmin.getWorkTypeId());

                        // 3. Fetch and map Office Details safely if officeId exists
                        if (listOfAdmin.getOfficeId() != null) {
                            officeRepository.findById(listOfAdmin.getOfficeId())
                                    .ifPresent(office -> {
                                        OfficeResponse officeResponse = new OfficeResponse(
                                                office.getId(),
                                                office.getOfficeName(),
                                                office.getLatitude(),
                                                office.getLongitude(),
                                                office.getHrEmpId(),
                                                office.getAddress(),
                                                office.getContact(),
                                                office.getGoogleMap()
                                        );
                                        adminResponse.setOfficeResponse(officeResponse);
                                    });
                        }
                        return adminResponse;
                    })
                    .toList();

            // 4. Wrap transformed list back into a cleanly typed PageResponse
            PageResponse<AdminResponse> finalPageResponse = new PageResponse<>(
                    transformedContent,
                    rawPageData.getPageNumber(),
                    rawPageData.getPageSize(),
                    rawPageData.getTotalElements(),
                    rawPageData.getTotalPages(),
                    rawPageData.isLast()
            );

            return new SingleResponse<>(finalPageResponse, CustomStatus.SUCCESS);

        } catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }
            throw new CustomException(cleanErrorMessage, HttpStatus.valueOf(e.status()));
        }
    }

    @Override
    public SingleResponse<List<EmployeeAttendanceHistoryResponse>> getDateWiseAttendanceRecords(DateWiseAttendanceRequest request) {
        if (request.getToDate().isBefore(request.getFromDate())) {
            throw new CustomException(null, CustomStatus.INVALID_DATE_RANGE, 409);
        }
        List<EmployeeAttendanceHistoryResponse> responses;
        try {
            ApiResponse<List<EmployeeAttendanceHistoryInternalResponse>> apiResponse = attendanceClient.getDateWiseAttendanceRecords(request);
            apiResponse.getData().forEach(attendance -> {
                log.info("Feign ID = {}", attendance.getId());
                log.info("Feign Attendance Type ID = {}", attendance.getAttendanceTypeId());
            });
            responses = apiResponse.getData().stream()
                    .map(attendance -> {

                        EmployeeAttendanceHistoryResponse response =
                                new EmployeeAttendanceHistoryResponse();

                        response.setId(attendance.getId());
                        response.setEmployeeId(attendance.getEmployeeId());
                        response.setCheckInTime(attendance.getCheckInTime());
                        response.setCheckOutTime(attendance.getCheckOutTime());
                        response.setAttendanceStatus(attendance.getAttendanceStatus());
                        response.setTotalWorkMin(attendance.getTotalWorkMin());
                        response.setAttendanceTypeId(attendance.getAttendanceTypeId());

                        return response;
                    })
                    .toList();

        } catch (FeignException e) { // TODO handle exception properly
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Attendance microservice call failed";
            CustomStatus fallbackStatus = CustomStatus.MICROSERVICE_CALL_FAILED;

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                }

                // Dynamically match the error nature to a proper business status
                if (e.status() == 404) {
                    fallbackStatus = CustomStatus.ATTENDANCE_RECORDS_NOT_FOUND;
                    throw new CustomException(
                            cleanErrorMessage,
                            fallbackStatus,
                            404
                    );
                } else if (e.status() == 405) {
                    fallbackStatus = CustomStatus.INVALID_REQUEST_FORMAT;
                }

            } catch (Exception parseException) {
                cleanErrorMessage = "Error parsing downstream service exception";
            }

            // 👈 Passing a verified fallbackStatus here eliminates the NullPointerException
            throw new CustomException(
                    cleanErrorMessage,
                    fallbackStatus,
                    409
            );
//            String rawErrorJson = fe.contentUTF8();
//            String cleanErrorMessage = "Microservices call failed";
//
//            try {
//                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
//                if (errorNode.has("message")) {
//                    cleanErrorMessage = errorNode.get("message").toString();
//                } else {
//                    cleanErrorMessage = rawErrorJson;
//                }
//            } catch (Exception parseException) {
//                cleanErrorMessage = rawErrorJson;
//            }
//
//            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//            if (fe.status() > 0) {
//                try {
//                    responseStatus = HttpStatus.valueOf(fe.status());
//                } catch (IllegalArgumentException ex) {
//                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
//                }
//            } else {
//                cleanErrorMessage = "Service is unreachable. Please try again later.";
//                responseStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503 Status
//            }
//            throw new CustomException(cleanErrorMessage, responseStatus);
        }
        return new SingleResponse<>(
                responses,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> getWeeklyAttendanceLogs(String employeeId) {
        WeeklyAttendanceLogsOfEmployeeRes responses = new WeeklyAttendanceLogsOfEmployeeRes();
        try {
            ApiResponse<WeeklyAttendanceLogsOfEmployeeRes> apiResponse = attendanceClient.getWeeklyAttendanceLogs(employeeId);
            if(apiResponse != null && apiResponse.getData() != null){
                responses = apiResponse.getData();
            }
        } catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Attendance microservice call failed";
            CustomStatus fallbackStatus = CustomStatus.MICROSERVICE_CALL_FAILED;

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                }

                // Dynamically match the error nature to a proper business status
                if (e.status() == 404) {
                    fallbackStatus = CustomStatus.ATTENDANCE_RECORDS_NOT_FOUND;
                    throw new CustomException(
                            cleanErrorMessage,
                            fallbackStatus,
                            404
                    );
                } else if (e.status() == 405) {
                    fallbackStatus = CustomStatus.INVALID_REQUEST_FORMAT;
                }

            } catch (Exception parseException) {
                cleanErrorMessage = "Error parsing downstream service exception";
            }
        }
        return new SingleResponse<>(
                responses,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> updateCheckoutRecordByEmpId(UpdateCheckOutRecordsRequest request) {

        ApiResponse<?> apiResponse;

        try {

            apiResponse = attendanceClient.updateCheckoutRecordByEmployeeId(request);

        } catch (FeignException e) {

            String cleanErrorMessage = "Attendance microservice call failed";
            CustomStatus fallbackStatus = CustomStatus.MICROSERVICE_CALL_FAILED;
            int statusCode = 500;

            String rawErrorJson = e.contentUTF8();

            // Get HTTP status from Feign
            if (e.status() > 0) {
                statusCode = e.status();
            }

            // Extract message from Attendance Service response
            try {

                if (rawErrorJson != null && !rawErrorJson.isBlank()) {

                    JsonNode errorNode = objectMapper.readTree(rawErrorJson);

                    if (errorNode.has("message")
                            && !errorNode.get("message").isNull()) {

                        cleanErrorMessage =
                                errorNode.get("message").asText();

                    }

                    else if (errorNode.has("response")
                            && errorNode.get("response").has("message")) {

                        cleanErrorMessage =
                                errorNode.get("response")
                                        .get("message")
                                        .asText();
                    }
                }

            } catch (Exception parseException) {

                log.error(
                        "Failed to parse Attendance Service error response: {}",
                        rawErrorJson,
                        parseException
                );

                cleanErrorMessage = "Attendance microservice call failed";
            }

            if (statusCode == 404) {

                fallbackStatus = CustomStatus.ATTENDANCE_RECORDS_NOT_FOUND;

            } else if (statusCode == 405) {

                fallbackStatus = CustomStatus.INVALID_REQUEST_FORMAT;

            } else if (statusCode == 400) {

                fallbackStatus = CustomStatus.INVALID_REQUEST_FORMAT;

            } else if (statusCode == 503) {

                fallbackStatus = CustomStatus.MICROSERVICE_CALL_FAILED;
            }
            throw new CustomException(
                    cleanErrorMessage,
                    fallbackStatus,
                    statusCode
            );
        }

        if (apiResponse == null) {
            throw new CustomException(
                    "Empty response received from Attendance Service",
                    CustomStatus.MICROSERVICE_CALL_FAILED,
                    502
            );
        }

        if (apiResponse.getStatus() != 200) {

            throw new CustomException(
                    apiResponse.getMessage(),
                    CustomStatus.MICROSERVICE_CALL_FAILED,
                    apiResponse.getStatus()
            );
        }

        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

}
