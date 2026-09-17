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

    @PostMapping("/getOtp") // TODO : SEPARATE APIs for generate first time registration and the forgot password
    public ResponseEntity<SingleResponse<?>> generateOtp(@Valid @RequestBody OtpRequest request){
        SingleResponse<?> response = userService.generateOtp(request);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/validateOtp")
    public ResponseEntity<SingleResponse<ValidationResponse>> validateOtp(@Valid @RequestBody ValidationRequest request){
        SingleResponse<ValidationResponse> response = userService.validateOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/completeRegistration")
    public ResponseEntity<SingleResponse<?>> completeRegistration(@Valid @RequestBody CompleteRegisterRequest request){
        SingleResponse<?> response = userService.completeRegistration(request);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<SingleResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request){
        SingleResponse<LoginResponse> response = userService.login(request);
        return  ResponseEntity.status(200).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<SingleResponse<?>> refresh(@RequestBody RefreshTokenRequest request){
        SingleResponse<?> response = refreshTokenService.getNewAccessToken(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getMasterDetails")
    public ResponseEntity<SingleResponse<MasterResponse>> getMasterDetails(){
        SingleResponse<MasterResponse> response = userService.getMasterDetails();
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<SingleResponse<?>> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        SingleResponse<?> response = userService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<SingleResponse<?>> updatePassword(@Valid @RequestBody UpdatePasswordRequest request, @RequestHeader ("X-User-Id") String userId){
        SingleResponse<?> response = userService.updatePassword(request,userId);
        return ResponseEntity.status(200).body(response);
    }

    @PatchMapping("/updateUserStatus/{userId}")
    public ResponseEntity<SingleResponse<String>> updateUserStatus(@PathVariable Long userId){
        SingleResponse<String> response = userService.updateUserStatus(userId);

        return ResponseEntity.status(200).body(response);
    }

    @PatchMapping("/updateUserIsDiscontinued/{userId}")
    public ResponseEntity<SingleResponse<String>> updateUserIsDiscontinued(@PathVariable Long userId){
        SingleResponse<String> response = userService.updateUserIsDiscontinued(userId);

        return ResponseEntity.status(200).body(response);
    }
}
