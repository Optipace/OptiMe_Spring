package com.employee.AdminService.client;

import com.employee.AdminService.dto.request.AuthIdentityPayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AUTH-SERVICE", url = "http://localhost:8081")
public interface AuthClient {
    @PostMapping("/api/auth/internal/create-identity")
    void createIdentity(@RequestBody AuthIdentityPayload payload);

    @DeleteMapping("/api/auth/internal/delete-identity/{employeeId}")
    void deleteIdentity(@PathVariable("employeeId") String employeeId);
}
