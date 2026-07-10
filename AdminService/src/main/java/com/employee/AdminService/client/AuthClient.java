package com.employee.AdminService.client;

import com.employee.AdminService.dto.request.AuthIdentityPayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//@FeignClient(name = "AUTH-SERVICE", url = "http://localhost:7071")
@FeignClient(name = "AUTH-SERVICE")
public interface AuthClient {
    @PostMapping("/api/auth/internal/create-identity")
    void createIdentity(@RequestBody AuthIdentityPayload payload);

    @DeleteMapping("/api/auth/internal/delete-identity/{employeeId}")
    void deleteIdentity(@PathVariable("employeeId") String employeeId);

    @PostMapping("/api/auth/internal/sendWelcomeEmail")
    void sendAccountCreatedEmail(@RequestParam("emailId") String emailId);
}
