package com.employee.AdminService.controller;

import com.employee.AdminService.dto.request.RegisterRequest;
import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UserController {

        private final AdminService adminService;

        @PostMapping("/add-user")
        public ResponseEntity<ApiResponse<?>> addNewUser(
                @Valid @RequestBody RegisterRequest request,
                @RequestHeader("X-Employee-Id") String adminEmployeeId) { // Supplied by API Gateway!

            ApiResponse<?> response = adminService.addNewUser(request, adminEmployeeId);

            return ResponseEntity.status(200).body(response);
        }
}
