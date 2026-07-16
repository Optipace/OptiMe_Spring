package com.employee.AdminService.controller;

import com.employee.AdminService.dto.request.*;
import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.FeedbackResponse;
import com.employee.AdminService.dto.response.OfficeResponse;
import com.employee.AdminService.dto.response.PageResponse;
import com.employee.AdminService.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/addUser")
    public ResponseEntity<ApiResponse<?>> addNewUser(
            @Valid @RequestBody RegisterRequest request,
            @RequestHeader("X-Employee-Id") String adminEmployeeId) { // Supplied by API Gateway!

        ApiResponse<?> response = adminService.addNewUser(request, adminEmployeeId);

        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/addOffice")
    public ResponseEntity<ApiResponse<?>> addNewOffice(@Valid @RequestBody AddNewOfficeRequest request){
        ApiResponse<?> response = adminService.addNewOffice(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getOfficeList")
    public ResponseEntity<ApiResponse<PageResponse<OfficeResponse>>> getOfficeList(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "5") int size){
        Pageable pageable = PageRequest.of(page, size);
        ApiResponse<PageResponse<OfficeResponse>> response = adminService.getOfficeList(pageable);
        return ResponseEntity.status(200).body(response);
    }

    @PatchMapping("/updateOffice")
    public ResponseEntity<ApiResponse<?>> updateOffice(@RequestBody OfficeRequest request){
        ApiResponse<?> response = adminService.updateOffice(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getOfficeNames")
    public ResponseEntity<ApiResponse<PageResponse<String>>> getOfficeNames(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "5")int size){
        Pageable pageable = PageRequest.of(page,size);
        ApiResponse<PageResponse<String>> response = adminService.getOfficeNames(pageable);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getFeedback")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedback(){
        ApiResponse<List<FeedbackResponse>> response = adminService.getFeedback();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/updateFeedback")
    public ResponseEntity<ApiResponse<?>> updateFeedback(@RequestBody FeedbackUpdateRequest request){
        ApiResponse<?> response = adminService.updateFeedback(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getAllAppliedLeaves")
    public ResponseEntity<ApiResponse<?>> getAllAppliedLeaves() {
        ApiResponse<?> response = adminService.getAllAppliedLeaves();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/messageForEmployees")
    public ResponseEntity<ApiResponse<?>> sendBroadcastMessage(@RequestBody NotificationRequest request) {
        ApiResponse<?> response = adminService.sendBroadcastMessage(request);
        return ResponseEntity.ok(response);
    }
}
