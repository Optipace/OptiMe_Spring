package com.employee.EmployeeProfileService.controller;


import com.employee.EmployeeProfileService.dto.request.FeedbackRequest;
import com.employee.EmployeeProfileService.dto.request.FeedbackUpdateRequest;
import com.employee.EmployeeProfileService.dto.request.UpdateEmployeeRequest;
import com.employee.EmployeeProfileService.dto.response.*;
import com.employee.EmployeeProfileService.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService empService;

    @GetMapping("/allEmp")
    public ResponseEntity<SingleResponse<List<ListOfEmployeeResponse>>> getAllEmployees(){
        SingleResponse<List<ListOfEmployeeResponse>> response = empService.getAllEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getEmployeeDetails")
    public ResponseEntity<SingleResponse<EmployeeResponse>> getEmployeeDetails(@RequestHeader ("X-Id") String employeeId){
        System.out.println(employeeId+"<-----id");
        SingleResponse<EmployeeResponse> response = empService.getEmployeeDetails(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getEmployeeByEmployeeId")
    public ResponseEntity<SingleResponse<EmployeeResponse>> getEmployeeByEmployeeId(@RequestParam("employeeId")String employeeId){
        SingleResponse<EmployeeResponse> response = empService.getEmployeeByEmployeeId(employeeId);
        return ResponseEntity.status((HttpStatus.OK)).body(response);
    }

    @GetMapping("/officeNames")
    public ResponseEntity<SingleResponse<?>> getOfficeNames(){
        SingleResponse<?> response = empService.getOfficeNames();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/upload/EmployeeProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SingleResponse<?>> uploadEmployeeImage(@RequestParam("image") MultipartFile file, @RequestHeader ("X-Id") String employeeId ){
        SingleResponse<?> response = empService.uploadEmployeeProfile(file,employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/saveFeedback")
    public ResponseEntity<SingleResponse<?>> saveFeedback(@RequestBody FeedbackRequest request, @RequestHeader ("X-Id") String employeeId){
        SingleResponse<?> response = empService.saveFeedback(request,employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getFeedback")
    public ResponseEntity<SingleResponse<List<FeedbackResponse>>> getFeedback(){
        SingleResponse<List<FeedbackResponse>> response = empService.getFeedback();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/updateFeedback")
    public ResponseEntity<SingleResponse<?>> updateFeedback(@RequestBody FeedbackUpdateRequest request){
        SingleResponse<?> response = empService.updateFeedback(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getAllAdmin")
    public ResponseEntity<SingleResponse<List<ListOfAdminResponse>>> getAllAdminDetails(){
        SingleResponse<List<ListOfAdminResponse>> response = empService.getAllAdminDetails();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
//    @GetMapping("/employeeProfile")
//    public ResponseEntity<Resource> getEmployeeProfile(@RequestHeader ("Authorization") String authHeader){
//        return empService.getEmployeeProfile(authHeader);
//    }

    @PostMapping(value="/uploadDocument",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SingleResponse<?>> uploadDocument(@RequestParam("file") MultipartFile file,
                                                            @RequestParam("employeeId") String employeeId,
                                                            @RequestParam("documentType") String documentType,
                                                            @RequestParam("documentNo") String documentNo,
                                                            @RequestHeader("X-User-Id") Long userId){
        SingleResponse<?> response = empService.uploadDocument(file,employeeId,documentType,documentNo,userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/updateEmployee")
    public ResponseEntity<SingleResponse<?>> updateEmployee(@Valid @RequestBody UpdateEmployeeRequest request){
        SingleResponse<?> response = empService.updateEmployee(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}