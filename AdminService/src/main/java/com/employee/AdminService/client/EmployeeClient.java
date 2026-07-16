package com.employee.AdminService.client;

import com.employee.AdminService.dto.request.EmployeeProfilePayload;
import com.employee.AdminService.dto.request.FeedbackUpdateRequest;
import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.FeedbackResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE", url = "http://localhost:7072")
@FeignClient(name = "EMPLOYEE-PROFILE-SERVICE")
public interface EmployeeClient {
    @PostMapping("/api/employee/internal/createProfile")
    void createProfile(@RequestBody EmployeeProfilePayload payload);

    @DeleteMapping("/api/employee/internal/deleteIdentity")
    void deleteIdentity(@RequestParam("employeeId") String employeeId);

    @GetMapping("/api/employee/internal/getFeedback")
    ApiResponse<List<FeedbackResponse>> getFeedback();

    @PutMapping("/api/employee/internal/updateFeedback")
    ApiResponse<?> updateFeedback(@RequestBody FeedbackUpdateRequest request);
}
