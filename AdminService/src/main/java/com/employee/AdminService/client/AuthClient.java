package com.employee.AdminService.client;

import com.employee.AdminService.dto.request.AuthIdentityPayload;
import com.employee.AdminService.dto.request.UpdateIdentityRequest;
import com.employee.AdminService.dto.response.NewUserResponse;
import com.employee.AdminService.dto.response.SingleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

//@FeignClient(name = "AUTH-SERVICE", url = "http://localhost:7071")
@FeignClient(name = "AUTH-SERVICE")
public interface AuthClient {
    @PostMapping("/api/auth/internal/createIdentity")
    SingleResponse<NewUserResponse> createIdentity(@RequestBody AuthIdentityPayload payload);

    @DeleteMapping("/api/auth/internal/deleteIdentity/{employeeId}")
    void deleteIdentity(@PathVariable("employeeId") String employeeId);

    @PutMapping("/api/auth/internal/updateIdentity")
    void updateIdentity(@RequestBody UpdateIdentityRequest request);
}
