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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/internal")
@RequiredArgsConstructor
public class EmployeeInternalController {

    private final EmployeeInternalService empInternalService;

    @PostMapping("/createProfile")
    public SingleResponse<?> createProfile(@Valid @RequestBody EmployeeProfileRequest request){
        return empInternalService.createProfile(request);
    }

    @GetMapping("/getProfile")
    public SingleResponse<EmployeeGetProfileResponse> getProfile(@RequestParam String employeeId){
        return empInternalService.getProfile(employeeId);
    }

    @PostMapping("/completeProfile")
    public ResponseEntity<SingleResponse<?>> completeProfile(@RequestBody CompleteProfileRequest request){
        SingleResponse<?> response = empInternalService.completeProfile(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getEmployeeByUserId")
    SingleResponse<Long> getEmployeeByUserId(@RequestParam Long userId){
        return empInternalService.getEmployeeByUserId(userId);
    }

    @GetMapping("/getMasterDetails")
    public SingleResponse<?> getMasterDetails(){
        return empInternalService.getMasterDetails();
    }

    @GetMapping("/checkEmployeeByEmployeeId")
    public boolean checkEmployeeByEmployeeId(@RequestParam("employeeId") Long employeeId){
        return empInternalService.checkEmployeeByEmployeeId(employeeId);
    }

    @PostMapping("/updateEmployeeStatus")
    public ResponseEntity<SingleResponse<?>> updateEmployeeStatus(@RequestBody UpdateEmployeeStatusRequest request) {
        SingleResponse<?> response = empInternalService.updateEmployeeStatus(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getEmployeeById")
    public SingleResponse<EmployeeInternalResponse> getEmployeeById(@RequestParam("employeeId")Long employeeId){
        return empInternalService.getEmployeeById(employeeId);
    }

    @DeleteMapping("/deleteIdentity")
    public SingleResponse<?> deleteIdentity(@RequestParam("employeeId") String employeeId){
        return empInternalService.deleteIdentity(employeeId);
    }

    @GetMapping("/getFeedback")
    public SingleResponse<List<FeedbackResponse>> getFeedback(){
        return empInternalService.getFeedback();
    }

    @PutMapping("/updateFeedback")
    public SingleResponse<?> updateFeedback(@RequestBody FeedbackUpdateRequest request){
        return empInternalService.updateFeedback(request);
    }

    @GetMapping("/checkWorkTypeById")
    public boolean getWorkTypeId(@RequestParam("workTypeId")Long workTypeId){
        return empInternalService.getWorkTypeId(workTypeId);
    }

    @GetMapping("/getAllEmployee")
    public SingleResponse<PageResponse<EmployeeResponse>> getAllEmployee(Pageable pageable){
        SingleResponse<PageResponse<EmployeeResponse>> response = empInternalService.getAllEmployee(pageable);
        return response;

    }

    @GetMapping("/getAllEmployeeId")
    public SingleResponse<ListOfEmployeeIdResponse> getAllEmployeeId(){
        return empInternalService.getAllEmployeeId();
    }

    @GetMapping("/isHrEmpId")
    public boolean isHrEmployeeId(@RequestParam("employeeId") Long hrEmpId){
        return empInternalService.isHrEmployeeId(hrEmpId);
    }

    @GetMapping("/getAllAdmin")
    public SingleResponse<PageResponse<ListOfAdminInternalResponse>> getAllAdminDetails(Pageable pageable){
        return empInternalService.getAllAdminDetails(pageable);
    }

    @GetMapping("/getEmployeeName")
    public SingleResponse<?> getEmployeeName(@RequestParam Long employeeId){
       return empInternalService.getEmployeeName(employeeId);
    }
}
