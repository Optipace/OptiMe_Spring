package com.employee.EmployeeProfileService.service.impl;

import com.employee.EmployeeProfileService.client.AdminClient;
import com.employee.EmployeeProfileService.client.AttendanceClient;
import com.employee.EmployeeProfileService.client.NotificationClient;
import com.employee.EmployeeProfileService.config.AppProperties;
import com.employee.EmployeeProfileService.dto.request.FeedbackRequest;
import com.employee.EmployeeProfileService.dto.request.FeedbackUpdateRequest;
import com.employee.EmployeeProfileService.dto.request.NotificationPayload;
import com.employee.EmployeeProfileService.dto.response.*;
import com.employee.EmployeeProfileService.enums.*;
import com.employee.EmployeeProfileService.exception.CustomException;
import com.employee.EmployeeProfileService.model.Employee;
import com.employee.EmployeeProfileService.model.Feedback;
import com.employee.EmployeeProfileService.repository.EmployeeRepository;
import com.employee.EmployeeProfileService.repository.FeedbackRepository;
import com.employee.EmployeeProfileService.service.EmployeeService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImplementation implements EmployeeService {
    private static final long MAX_IMAGE_SIZE = 1024 * 1024; // MAX 1MB

    private final EmployeeRepository employeeRepository;

    private final ModelMapper mapperModel;

    private final AppProperties appProperties;

    private final FeedbackRepository feedbackRepository;

    private final AttendanceClient attendanceClient;

    private final NotificationClient notificationClient;

    private final ObjectMapper objectMapper;

    private final AdminClient adminClient;

    @Override
    public SingleResponse<List<ListOfEmployeeResponse>> getAllEmployees() {
        List<Employee> employeeList = employeeRepository.findAll();

        List<ListOfEmployeeResponse> employeeResponse = employeeList.stream()
                .map((employee) ->{

                    log.info("Calling admin Service to get Office details for office Id {} for the employee {}",employee.getOfficeId(), employee.getEmployeeId());
                    ApiResponse<OfficeResponse> apiOfficeResponse = adminClient.getOfficeDetails(employee.getOfficeId());
                    OfficeResponse officeResponse = new OfficeResponse();

                    if(apiOfficeResponse.getData() != null){
                        officeResponse = mapperModel.map(apiOfficeResponse.getData(), OfficeResponse.class);
                    }

                    ListOfEmployeeResponse response = mapperModel.map(employee, ListOfEmployeeResponse.class);
                    response.setDesignationId(employee.getDesignation().getId());

//                    if (employee.getEmployeeProfilePath() != null) {
//                        try {
//                            File file = new File(employee.getEmployeeProfilePath());
//                            if (file.exists() && file.canRead()) {
//                                byte[] fileBytes = Files.readAllBytes(file.toPath());
//                                String encodedString = Base64.getEncoder().encodeToString(fileBytes);
//                                response.setImage(encodedString);
//                            }
//                        } catch (IOException e) {
//                            response.setImage(null);
//                        }
//                    }

                    response.setOfficeId(officeResponse.getOfficeId());
                    response.setOfficeName(officeResponse.getOfficeName());

                    return response;
                })
                .toList();

        return new SingleResponse<>(
                employeeResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<EmployeeResponse> getEmployeeDetails(String employeeId) {

        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_NOT_FOUND, 201));

        EmployeeResponse response = mapperModel.map(employee, EmployeeResponse.class);
        try {

            log.info("Calling Admin service for office response");
            ApiResponse<OfficeResponse> officeApiResponse = adminClient.getOfficeDetails(employee.getOfficeId());
            log.info("Received response from Admin service");

            log.info("Attendance service is calling");
            ApiResponse<?> apiResponse = attendanceClient.getAttendanceStatus(employeeId);
            log.info("Attendance service called");

            NotificationPayload payload = new NotificationPayload(
                    employeeId,
                    "Employee details fetched",
                    " " + employeeId + " details",
                    "INFO"
            );

            log.info("Notification service calling");
            notificationClient.sendPrivateNotification(payload);
            log.info("Notification service called");

            if (apiResponse.getData() == null) {
                response.setAttendanceStatus(null);
            } else {
                response.setAttendanceStatus(apiResponse.getData().toString());
            }

            if(officeApiResponse.getData() != null){
                response.setOffice(officeApiResponse.getData());
            }else{
                response.setOffice(null);
            }
//            if(apiResponse != null || apiResponse.getData() != null){
//                response.setAttendanceStatus(apiResponse.getData().toString());
//            }

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


//        if (office != null) {
//            OfficeResponse officeResponse = mapperModel.map(office, OfficeResponse.class);
//            response.setOffice(officeResponse);
//        } else {
//            response.setOffice(null);
//        }

        if (employee.getEmployeeProfilePath() != null) {
            try {
                File file = new File(employee.getEmployeeProfilePath());
                if (file.exists() && file.canRead()) {
                    byte[] fileBytes = Files.readAllBytes(file.toPath());
                    String encodedString = Base64.getEncoder().encodeToString(fileBytes);
                    response.setImage(encodedString);
                }
            } catch (IOException e) {
                response.setImage(null);
            }
        }

        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<EmployeeResponse> getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_NOT_FOUND, 201));

        EmployeeResponse response = mapperModel.map(employee, EmployeeResponse.class);
        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public ListResponse<?> getOfficeNames() {
        ApiResponse<List<String>> apiOfficeNames = adminClient.getOfficeNames();
        List<String> officeNames = null;
        if(apiOfficeNames != null && apiOfficeNames.getData() != null){
            officeNames = apiOfficeNames.getData();
        }
        return new ListResponse<>(
                officeNames,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> uploadEmployeeProfile(MultipartFile file, String employeeId) {

        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_NOT_FOUND, 201));

        if (file.isEmpty())
            throw new CustomException(null, CustomStatus.FILE_IS_EMPTY, 201);

        log.info("Incoming file size {}", file.getSize());
        if (file.getSize() > MAX_IMAGE_SIZE)
            throw new CustomException(null, CustomStatus.IMAGE_SIZE_EXCEEDED, 201);

        String contentType = file.getContentType();

        log.info("Incoming content type {}", contentType);

        if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType) || "image/jpg".equals(contentType))) {
            throw new CustomException(null, CustomStatus.INVALID_IMAGE_FORMAT, 201);
        }

        String filePath = saveFile(file, appProperties.getImage().getUploadDir() + "EmployeeProfile/" + employeeId + "/");
        log.info("Image saved path {}", filePath);
        employee.setEmployeeProfilePath(filePath);

        employeeRepository.save(employee);
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> saveFeedback(FeedbackRequest request, String employeeId) {

        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_NOT_FOUND, 201));

        Feedback feedback = new Feedback();
        if (request.getFeedbackStatus().equals(FeedbackEnum.Y) || request.getFeedbackStatus() == FeedbackEnum.Y) {
            feedback.setFeedback(request.getFeedback());
            feedback.setEmployeeName(employee.getEmployeeName());
            feedback.setStatusEnum(FeedbackStatusEnum.PENDING);
            feedbackRepository.save(feedback);
        } else {
            feedback.setFeedback(request.getFeedback());
            feedback.setEmployeeName(null);
            feedback.setStatusEnum(FeedbackStatusEnum.PENDING);
            feedbackRepository.save(feedback);
        }
        return new SingleResponse<>(
               null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<List<FeedbackResponse>> getFeedback() {
        List<Feedback> feedbackList = feedbackRepository.findAll();

        List<FeedbackResponse> responseList = feedbackList.stream()
                .map(f -> {
                    String employeeName = (f.getEmployeeName() == null || f.getEmployeeName().isBlank())
                            ? "*****"
                            : f.getEmployeeName();

                    return new FeedbackResponse(f.getId(), employeeName, f.getFeedback(), f.getStatusEnum());
                })
                .toList();

        return new SingleResponse<>(
                responseList,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> updateFeedback(FeedbackUpdateRequest request) {
        Feedback feedback = feedbackRepository.findById(request.getFeedbackId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.FEEDBACK_NOT_FOUND, 201));

        if (request.getFeedbackStatus().equals(FeedbackStatusEnum.PENDING)) {
            feedback.setStatusEnum(request.getFeedbackStatus());
            feedbackRepository.save(feedback);

            return new SingleResponse<>(
                    null,
                   CustomStatus.SUCCESS
            );
        } else {
            feedback.setStatusEnum(request.getFeedbackStatus());
            feedbackRepository.save(feedback);
        }

        return new SingleResponse<>(
               null,
                CustomStatus.SUCCESS
        );
    }

//    @Override
//    public ResponseEntity<Resource> getEmployeeProfile(String authHeader) {
//        if(authHeader == null || !authHeader.startsWith("Bearer ")){
//            throw new CustomException("Invalid token or please provide token", HttpStatus.BAD_REQUEST);
//        }
//
//        String token = authHeader.substring(7);
//
//        String employeeId = jwtUtil.extractEmployeeId(token);
//
//        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
//                .orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));
//
//        if(employee.getEmployeeProfilePath() == null)
//            throw new CustomException("No employee profile found", HttpStatus.NOT_FOUND);
//
//        File file = new File(employee.getEmployeeProfilePath());
//
//
//        if(!file.exists() || !file.canRead()){
//            throw new CustomException("Image file not found", HttpStatus.NOT_FOUND);
//        }
//
//        Resource resource = new FileSystemResource(file);
//
//        HttpHeaders httpHeaders = new HttpHeaders();
//
//        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        httpHeaders.setContentLength(file.length());
//
//        return ResponseEntity.ok().headers(httpHeaders).body(resource);
//    }

    private String saveFile(MultipartFile file, String folder) {
        try {

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(folder + fileName);

            Files.createDirectories(path.getParent());

            // Use streaming (better for large files)
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return path.toString(); // return file path

        } catch (IOException e) {
            throw new CustomException(null, CustomStatus.FILE_UPLOAD_FAILED, 201);
        }
    }
}
