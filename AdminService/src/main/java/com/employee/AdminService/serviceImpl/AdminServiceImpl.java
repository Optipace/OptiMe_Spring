package com.employee.AdminService.serviceImpl;

import com.employee.AdminService.client.AuthClient;
import com.employee.AdminService.client.EmployeeClient;
import com.employee.AdminService.client.LeaveClient;
import com.employee.AdminService.client.NotificationClient;
import com.employee.AdminService.dto.request.*;
import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.LeaveResponse;
import com.employee.AdminService.exception.CustomException;
import com.employee.AdminService.repository.OfficeRepository;
import com.employee.AdminService.service.AdminService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final AuthClient authClient;

    private final EmployeeClient employeeClient;

    private final ObjectMapper objectMapper;

    private final NotificationClient notificationClient;

    private final LeaveClient leaveClient;

    private final ModelMapper modelMapper;

    private final OfficeRepository officeRepository;

    @Override
    public ApiResponse<?> addNewUser(RegisterRequest request, String adminEmployeeId) {

        officeRepository.findById(request.getOfficeId())
                .orElseThrow(() -> new CustomException("Office Id not found", HttpStatus.NOT_FOUND));

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
                request.getDesignation(),
                request.getRole(),
                request.getGender(),
                request.getWorkType(),
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
            notificationClient.sendAccountCreatedEmail(request.getEmailId());
            log.info("Triggered account created email");
            log.info("Communication service is called to send welcome email");

            // 6. Sending broadcast notification to ALL
            NotificationPayload payload = new NotificationPayload();
            payload.setEmployeeId("ALL");
            payload.setTitle("Company Announcement");
            payload.setMessage("Please welcome our new employee: " + request.getEmployeeName());
            payload.setType("INFO");

            notificationClient.sendBroadCastNotification(payload);
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
        return new ApiResponse<>(
                true,
                "Employee added successfully",
                null,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<?> getAllAppliedLeaves() {

        List<LeaveResponse> leaveResponses = null;
        try {
            ApiResponse<List<LeaveResponse>> apiResponse = leaveClient.getAllAppliedLeaves();
            log.info("LEAVE SERVICE CALLED");
            if (apiResponse != null && apiResponse.getData() != null) {
                leaveResponses = apiResponse.getData().stream()
                        .map(l -> modelMapper.map(l, LeaveResponse.class))
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
        return new ApiResponse<>(
                true,
                "Applied Leaves",
                leaveResponses,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<?> sendBroadcastMessage(NotificationRequest request) {
        NotificationPayload payload = new NotificationPayload();
        payload.setEmployeeId("ALL");
        payload.setTitle(request.getTitle());
        payload.setMessage(request.getMessage());
        payload.setType(request.getType());

        notificationClient.sendBroadCastNotification(payload);

        return new ApiResponse<>(
                true,
                "Message delivered successfully",
                null,
                LocalDateTime.now(),
                200
        );
    }

}
