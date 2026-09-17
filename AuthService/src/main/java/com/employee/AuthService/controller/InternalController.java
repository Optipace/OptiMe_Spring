package com.employee.AuthService.controller;

import com.employee.AuthService.dto.request.AuthIdentityRequest;
import com.employee.AuthService.dto.request.UpdateIdentityRequest;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.dto.response.NewUserResponse;
import com.employee.AuthService.dto.response.SingleResponse;
import com.employee.AuthService.dto.response.UserStatusGatewayResponse;
import com.employee.AuthService.enums.UserStatusEnum;
import com.employee.AuthService.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/internal")
@RequiredArgsConstructor
public class InternalController {

    private final InternalService internalService;

    @PostMapping("/createIdentity")
    public ResponseEntity<SingleResponse<NewUserResponse>> createIdentity(@RequestBody AuthIdentityRequest request){
        SingleResponse<NewUserResponse> response = internalService.createIdentity(request);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/deleteIdentity/{employeeId}")
    public ResponseEntity<ApiResponse<?>> deleteIdentity(@PathVariable String employeeId){
        ApiResponse<?> response = internalService.deleteIdentity(employeeId);
        return ResponseEntity.status(200).body(response);
    }
    @PutMapping("/updateIdentity")
    public ResponseEntity<SingleResponse<?>> updateIdentity(@RequestBody UpdateIdentityRequest request){
        SingleResponse<?> response = internalService.updateIdentity(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/userStatus/{id}")
    public UserStatusGatewayResponse getUserStatus(@PathVariable Long id){
        return internalService.getUserStatus(id);
    }

}
