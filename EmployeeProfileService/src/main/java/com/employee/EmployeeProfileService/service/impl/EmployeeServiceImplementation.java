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
//import com.employee.EmployeeProfileService.model.Office;
import com.employee.EmployeeProfileService.repository.EmployeeRepository;
import com.employee.EmployeeProfileService.repository.FeedbackRepository;
//import com.employee.EmployeeProfileService.repository.OfficeRepository;
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
import java.time.LocalDateTime;
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
    public ApiResponse<List<ListOfEmployeeResponse>> getAllEmployees() {
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

        return new ApiResponse<>(
                true,
                "List of employees",
                employeeResponse,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<EmployeeResponse> getEmployeeDetails(String employeeId) {

        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

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

        return new ApiResponse<>(
                true,
                "Employee Details",
                response,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<EmployeeResponse> getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

        EmployeeResponse response = mapperModel.map(employee, EmployeeResponse.class);
        return new ApiResponse<>(
                true,
                "Employee Details",
                response,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<?> getOfficeNames() {
        ApiResponse<List<String>> apiOfficeNames = adminClient.getOfficeNames();
        List<String> officeNames = null;
        if(apiOfficeNames != null && apiOfficeNames.getData() != null){
            officeNames = apiOfficeNames.getData();
        }
        return new ApiResponse<>(
                true,
                "ACTIVE office names list",
                officeNames,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<?> uploadEmployeeProfile(MultipartFile file, String employeeId) {

        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

        if (file.isEmpty())
            throw new CustomException("File is empty", HttpStatus.BAD_REQUEST);

        log.info("Incoming file size {}", file.getSize());
        if (file.getSize() > MAX_IMAGE_SIZE)
            throw new CustomException("Image exceeds 1MB limit", HttpStatus.BAD_REQUEST);

        String contentType = file.getContentType();

        log.info("Incoming content type {}", contentType);

        if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType) || "image/jpg".equals(contentType))) {
            throw new CustomException("Only JPEG or PNG files are allowed", HttpStatus.BAD_REQUEST);
        }

        String filePath = saveFile(file, appProperties.getImage().getUploadDir() + "EmployeeProfile/" + employeeId + "/");
        log.info("Image saved path {}", filePath);
        employee.setEmployeeProfilePath(filePath);

        employeeRepository.save(employee);
        return new ApiResponse<>(
                true,
                "Image uploaded successfully",
                null,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<?> saveFeedback(FeedbackRequest request, String employeeId) {

        Employee employee = employeeRepository.findEmployeeByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

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
        return new ApiResponse<>(
                true,
                "Feedback saved successfully",
                null,
                LocalDateTime.now(),
                201
        );
    }

    @Override
    public ApiResponse<List<FeedbackResponse>> getFeedback() {
        List<Feedback> feedbackList = feedbackRepository.findAll();

        List<FeedbackResponse> responseList = feedbackList.stream()
                .map(f -> {
                    String employeeName = (f.getEmployeeName() == null || f.getEmployeeName().isBlank())
                            ? "*****"
                            : f.getEmployeeName();

                    return new FeedbackResponse(f.getId(), employeeName, f.getFeedback(), f.getStatusEnum());
                })
                .toList();

        return new ApiResponse<List<FeedbackResponse>>(
                true,
                "Feedback lists",
                responseList,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<?> updateFeedback(FeedbackUpdateRequest request) {
        Feedback feedback = feedbackRepository.findById(request.getFeedbackId())
                .orElseThrow(() -> new CustomException("Feedback not found! Please recheck the given feedback", HttpStatus.NOT_FOUND));

        if (request.getFeedbackStatus().equals(FeedbackStatusEnum.PENDING)) {
            feedback.setStatusEnum(request.getFeedbackStatus());
            feedbackRepository.save(feedback);

            return new ApiResponse<>(
                    true,
                    "Feedback is still PENDING",
                    null,
                    LocalDateTime.now(),
                    200
            );
        } else {
            feedback.setStatusEnum(request.getFeedbackStatus());
            feedbackRepository.save(feedback);
        }

        return new ApiResponse<>(
                true,
                "Feedback is " + request.getFeedbackStatus(),
                null,
                LocalDateTime.now(),
                200
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
            throw new CustomException("File upload failed", HttpStatus.BAD_REQUEST);
        }
    }
}
