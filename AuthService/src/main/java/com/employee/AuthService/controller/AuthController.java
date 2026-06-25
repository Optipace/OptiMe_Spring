package com.employee.AuthService.controller;

import com.employee.AuthService.dto.request.*;
import com.employee.AuthService.dto.response.*;
import com.employee.AuthService.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/getOtp")
    public ResponseEntity<ApiResponse<?>> generateOtp(@Valid @RequestBody OtpRequest request){
        ApiResponse<?> response = userService.generateOtp(request);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/validateOtp")
    public ResponseEntity<ApiResponse<?>> validateOtp(@Valid @RequestBody ValidationRequest request){
        ApiResponse<?> response = userService.validateOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete-registration")
    public ResponseEntity<ApiResponse<?>> completeRegistration(@Valid @RequestBody CompleteRegisterRequest request){
        ApiResponse<?> response = userService.completeRegistration(request);
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

    @GetMapping("/getMasterDetails")
    public ResponseEntity<ApiResponse<?>> getMasterDetails(){
        ApiResponse<?> response = userService.getMasterDetails();
        return ResponseEntity.status(200).body(response);
    }
}
