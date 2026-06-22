package com.employee.AuthService.controller;

import com.employee.AuthService.dto.request.AuthIdentityRequest;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth/internal")
@RequiredArgsConstructor
public class InternalController {

    private final InternalService internalService;

    @PostMapping("/create-identity")
    public ResponseEntity<ApiResponse<?>> createIdentity(@RequestBody AuthIdentityRequest request){
        ApiResponse<?> response = internalService.createIdentity(request);
        return ResponseEntity.status(200).body(response);
    }
}
