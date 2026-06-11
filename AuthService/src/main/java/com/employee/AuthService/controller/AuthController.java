package com.employee.AuthService.controller;

import com.employee.AuthService.dto.request.*;
import com.employee.AuthService.dto.response.*;
import com.employee.AuthService.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/getOtp")
    public ResponseEntity<ApiResponse<?>> getOtpByIdentifier(@Valid @RequestBody OtpRequest request){
        ApiResponse<?> response = userService.getOtpByIdentifier(request.getIdentifier());
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/validateOtp")
    public ResponseEntity<ApiResponse<?>> validateOtp(@Valid @RequestBody ValidationRequest request){
        ApiResponse<?> response = userService.validateOtp(request.getIdentifier(),request.getOtp());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@Valid @RequestBody RegisterRequest request){
        ApiResponse<?> response = userService.registerUser(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request){
        ApiResponse<LoginResponse> response = userService.login(request);
        return  ResponseEntity.status(200).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refresh(@RequestBody RefreshTokenRequest request){
        ApiResponse<?> response = refreshTokenService.getNewAccessToken(request);
        return ResponseEntity.status(200).body(response);
    }
}
