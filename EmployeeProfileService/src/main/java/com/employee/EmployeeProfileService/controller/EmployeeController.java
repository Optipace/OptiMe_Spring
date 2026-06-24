package com.employee.EmployeeProfileService.controller;


import com.employee.EmployeeProfileService.dto.request.FeedbackRequest;
import com.employee.EmployeeProfileService.dto.request.FeedbackUpdateRequest;
import com.employee.EmployeeProfileService.dto.response.*;
import com.employee.EmployeeProfileService.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService empService;

    @GetMapping("/allEmp")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAllEmployees(){
        ApiResponse<List<EmployeeResponse>> response = empService.getAllEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getEmployeeDetails")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeDetails(@RequestHeader ("X-Employee-Id") String employeeId){
        ApiResponse<EmployeeResponse> response = empService.getEmployeeDetails(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getEmployeeByID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByEmployeeId(@RequestParam("employeeId")String employeeId){
        ApiResponse<EmployeeResponse> response = empService.getEmployeeByEmployeeId(employeeId);
        return ResponseEntity.status((HttpStatus.OK)).body(response);
    }

    @GetMapping("/officeNames")
    public ResponseEntity<ApiResponse<?>> getOfficeNames(){
        ApiResponse<?> response = empService.getOfficeNames();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/upload/EmployeeProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> uploadEmployeeImage(@RequestParam("image") MultipartFile file, @RequestHeader ("X-Employee-Id") String employeeId ){
        ApiResponse<?> response = empService.uploadEmployeeProfile(file,employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/saveFeedback")
    public ResponseEntity<ApiResponse<?>> saveFeedback(@RequestBody FeedbackRequest request, @RequestHeader ("X-Employee-Id") String employeeId){
        ApiResponse<?> response = empService.saveFeedback(request,employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getFeedback")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedback(){
        ApiResponse<List<FeedbackResponse>> response = empService.getFeedback();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/updateFeedback")
    public ResponseEntity<ApiResponse<?>> updateFeedback(@RequestBody FeedbackUpdateRequest request){
        ApiResponse<?> response = empService.updateFeedback(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


//    @GetMapping("/employeeProfile")
//    public ResponseEntity<Resource> getEmployeeProfile(@RequestHeader ("Authorization") String authHeader){
//        return empService.getEmployeeProfile(authHeader);
//    }
}