package com.employee.EmployeeProfileService.controller;

import com.employee.EmployeeProfileService.dto.request.CompleteProfileRequest;
import com.employee.EmployeeProfileService.dto.request.EmployeeProfileRequest;
import com.employee.EmployeeProfileService.dto.request.FeedbackUpdateRequest;
import com.employee.EmployeeProfileService.dto.request.UpdateEmployeeStatusRequest;
import com.employee.EmployeeProfileService.dto.response.*;
import com.employee.EmployeeProfileService.service.EmployeeInternalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/internal")
@RequiredArgsConstructor
public class EmployeeInternalController {

    private final EmployeeInternalService empInternalService;

    @PostMapping("/createProfile")
    public ResponseEntity<ApiResponse<?>> createProfile(@Valid @RequestBody EmployeeProfileRequest request){
        ApiResponse<?> response = empInternalService.createProfile(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getProfile")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getProfile(@RequestParam String employeeId){
        ApiResponse<EmployeeResponse> response = empInternalService.getProfile(employeeId);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/completeProfile")
    public ResponseEntity<ApiResponse<?>> completeProfile(@RequestBody CompleteProfileRequest request){
        ApiResponse<?> response = empInternalService.completeProfile(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getMasterDetails")
    public ResponseEntity<ApiResponse<?>> getMasterDetails(){
        ApiResponse<?> response = empInternalService.getMasterDetails();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/checkEmployeeByEmployeeId")
    public boolean checkEmployeeByEmployeeId(@RequestParam("employeeId") String employeeId){
        return empInternalService.checkEmployeeByEmployeeId(employeeId);
    }

    @PostMapping("/updateEmployeeStatus")
    public ResponseEntity<ApiResponse<?>> updateEmployeeStatus(@RequestBody UpdateEmployeeStatusRequest request) {
        ApiResponse<?> response = empInternalService.updateEmployeeStatus(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getEmployeeByEmployeeId")
    public ResponseEntity<ApiResponse<EmployeeInternalResponse>> getEmployeeByEmployeeId(@RequestParam("employeeId")String employeeId){
        ApiResponse<EmployeeInternalResponse> response = empInternalService.getEmployeeByEmployeeId(employeeId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteIdentity")
    public ResponseEntity<ApiResponse<?>> deleteIdentity(@RequestParam("employeeId") String employeeId){
        ApiResponse<?> response = empInternalService.deleteIdentity(employeeId);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getFeedback")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedback(){
        ApiResponse<List<FeedbackResponse>> response = empInternalService.getFeedback();
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/updateFeedback")
    public ResponseEntity<ApiResponse<?>> updateFeedback(@RequestBody FeedbackUpdateRequest request){
        ApiResponse<?> response = empInternalService.updateFeedback(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/checkWorkTypeById")
    public boolean getWorkTypeId(@RequestParam("workTypeId")Long workTypeId){
        return empInternalService.getWorkTypeId(workTypeId);
    }

    @GetMapping("/getAllEmployee")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponse>>> getAllEmployee(Pageable pageable){
        ApiResponse<PageResponse<EmployeeResponse>> response = empInternalService.getAllEmployee(pageable);
        return ResponseEntity.status(200).body(response);

    }

    @GetMapping("/getAllEmployeeId")
    public ResponseEntity<ApiResponse<ListOfEmployeeIdResponse>> getAllEmployeeId(){
        ApiResponse<ListOfEmployeeIdResponse> response = empInternalService.getAllEmployeeId();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/isHrEmpId")
    public boolean isHrEmployeeId(@RequestParam("employeeId") String hrEmpId){
        return empInternalService.isHrEmployeeId(hrEmpId);
    }

}
