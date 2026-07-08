package com.employee.AuthService.controller;

import com.employee.AuthService.dto.request.AuthIdentityRequest;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/internal")
@RequiredArgsConstructor
public class InternalController {

    private final InternalService internalService;

    @PostMapping("/create-identity")
    public ResponseEntity<ApiResponse<?>> createIdentity(@RequestBody AuthIdentityRequest request){
        ApiResponse<?> response = internalService.createIdentity(request);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/delete-identity/{employeeId}")
    public ResponseEntity<ApiResponse<?>> deleteIdentity(@PathVariable String employeeId){
        ApiResponse<?> response = internalService.deleteIdentity(employeeId);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/sendWelcomeEmail")
    public ResponseEntity<String> sendAccountCreatedEmail(@RequestParam("emailId") String emailId){
        internalService.sendAccountCreatedEmail(emailId);
        return ResponseEntity.ok("Email processed");
    }
}
