package com.employee.AdminService.client;

import com.employee.AdminService.dto.request.EmployeeProfilePayload;
import com.employee.AdminService.dto.request.FeedbackUpdateRequest;
import com.employee.AdminService.dto.response.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
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
    SingleResponse<List<FeedbackResponse>> getFeedback();

    @PutMapping("/api/employee/internal/updateFeedback")
    SingleResponse<?> updateFeedback(@RequestBody FeedbackUpdateRequest request);

    @GetMapping("/api/employee/internal/getMasterDetails")
    SingleResponse<MasterEmployeeResponse> getMasterDetails();

    @GetMapping("/api/employee/internal/getAllEmployee")
    SingleResponse<PageResponse<EmployeeResponse>> getAllEmployee(Pageable pageable);

    @GetMapping("/api/employee/internal/isHrEmpId")
    boolean isHrEmployeeId(@RequestParam("employeeId")Long hrEmpId);

    @GetMapping("/api/employee/internal/getAllAdmin")
    SingleResponse<PageResponse<ListOfAdminResponse>> getAllAdmin(Pageable pageable);
}
