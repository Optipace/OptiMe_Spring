package com.employee.AdminService.serviceImpl;

import com.employee.AdminService.client.AuthClient;
import com.employee.AdminService.client.EmployeeClient;
import com.employee.AdminService.dto.request.AuthIdentityPayload;
import com.employee.AdminService.dto.request.EmployeeProfilePayload;
import com.employee.AdminService.dto.request.RegisterRequest;
import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.exception.CustomException;
import com.employee.AdminService.service.AdminService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final AuthClient authClient;
    private final EmployeeClient  employeeClient;
    private final ObjectMapper objectMapper;

    @Override
    public ApiResponse<?> addNewUser(RegisterRequest request, String adminEmployeeId) {

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

           try{
               // 3. Call Auth service via Feign
               authClient.createIdentity(authPayload);
               log.info("Auth client is called");
               isAuthCreated = true;

               // 4. Call Employee Profile service via Feign
               employeeClient.createProfile(profilePayload);

           }catch (FeignException e){
               if(isAuthCreated){
                   try{
                       authClient.deleteIdentity(request.getEmployeeId());
                   }catch (Exception ex){
                       log.error("Rollback failed {} ",ex.getMessage());
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

}
