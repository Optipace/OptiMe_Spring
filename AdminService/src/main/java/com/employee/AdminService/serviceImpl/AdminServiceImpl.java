package com.employee.AdminService.serviceImpl;

import com.employee.AdminService.dto.request.InternalRequests;
import com.employee.AdminService.dto.request.RegisterRequest;
import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Service
public class AdminServiceImpl implements AdminService {
    private final RestClient restClient = RestClient.create();// Spring Boot 3+ modern HTTP Client

    @Override
    public ApiResponse<?> addNewUser(RegisterRequest request, String adminEmployeeId) {

            // 1. Prepare Auth Payload (Security Data)
            InternalRequests.AuthIdentityPayload authPayload = new InternalRequests.AuthIdentityPayload(
                    request.getEmployeeId(),
                    request.getEmailId(),
                    request.getContact(),
                    request.getRole(),
                    adminEmployeeId // The logged-in admin who is making this request
            );

            // 2. Prepare Profile Payload (HR Data)
            InternalRequests.EmployeeProfilePayload profilePayload = new InternalRequests.EmployeeProfilePayload(
                    request.getEmployeeId(),
                    request.getUserName(),
                    request.getContact(),
                    request.getEmailId(),
                    request.getDesignation(),
                    request.getRole(),
                    request.getGender(),
                    request.getWorkType(),
                    request.getOfficeId()
            );

            // 3. Make Synchronous Call to Auth Service (Port 8081)
            // If this fails, an exception is thrown and the process stops
            restClient.post()
                    .uri("http://localhost:8081/api/auth/internal/create-identity")
                    .body(authPayload)
                    .retrieve()
                    .toBodilessEntity();

            // 4. Make Synchronous Call to Employee Profile Service (Port 8082)
            restClient.post()
                    .uri("http://localhost:8082/api/employee/internal/create-profile")
                    .body(profilePayload)
                    .retrieve()
                    .toBodilessEntity();

            return new ApiResponse<>(
                    true,
                    "User added successfully",
                    null,
                    LocalDateTime.now(),
                    200
            );
    }

}
