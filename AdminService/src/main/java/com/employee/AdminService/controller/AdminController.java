package com.employee.AdminService.controller;

import com.employee.AdminService.dto.request.*;
import com.employee.AdminService.dto.response.*;
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
    public ResponseEntity<SingleResponse<?>> addNewUser(
            @Valid @RequestBody RegisterRequest request,
            @RequestHeader("X-Employee-Id") String adminEmployeeId) { // Supplied by API Gateway!

        SingleResponse<?> response = adminService.addNewUser(request, adminEmployeeId);

        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getAllemployee")
    public ResponseEntity<SingleResponse<PageResponse<EmployeeResponse>>> getAllEmployee(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "5")int size){
        Pageable pageable = PageRequest.of(page, size);
        SingleResponse<PageResponse<EmployeeResponse>> response = adminService.getAllEmployee(pageable);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/addOffice")
    public ResponseEntity<SingleResponse<?>> addNewOffice(@Valid @RequestBody AddNewOfficeRequest request){
        SingleResponse<?> response = adminService.addNewOffice(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getOfficeList")
    public ResponseEntity<SingleResponse<PageResponse<OfficeResponse>>> getOfficeList(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "5") int size){
        Pageable pageable = PageRequest.of(page, size);
        SingleResponse<PageResponse<OfficeResponse>> response = adminService.getOfficeList(pageable);
        return ResponseEntity.status(200).body(response);
    }

    @PatchMapping("/updateOffice")
    public ResponseEntity<SingleResponse<?>> updateOffice(@RequestBody OfficeRequest request){
        SingleResponse<?> response = adminService.updateOffice(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getOfficeNames")
    public ResponseEntity<SingleResponse<PageResponse<String>>> getOfficeNames(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "5")int size){
        Pageable pageable = PageRequest.of(page,size);
        SingleResponse<PageResponse<String>> response = adminService.getOfficeNames(pageable);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getFeedback")
    public ResponseEntity<SingleResponse<List<FeedbackResponse>>> getFeedback(){
        SingleResponse<List<FeedbackResponse>> response = adminService.getFeedback();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/updateFeedback")
    public ResponseEntity<SingleResponse<?>> updateFeedback(@RequestBody FeedbackUpdateRequest request){
        SingleResponse<?> response = adminService.updateFeedback(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/getAllAppliedLeaves")
    public ResponseEntity<SingleResponse<?>> getAllAppliedLeaves() {
        SingleResponse<?> response = adminService.getAllAppliedLeaves();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/messageForEmployees")
    public ResponseEntity<SingleResponse<?>> sendBroadcastMessage(@RequestBody NotificationRequest request) {
        SingleResponse<?> response = adminService.sendBroadcastMessage(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getMasterDetails")
    public ResponseEntity<SingleResponse<MasterResponse>> getMasterDetails(){
        SingleResponse<MasterResponse> response = adminService.getMasterDetails();
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/updateOfficeStatus")
    public ResponseEntity<SingleResponse<?>> updateOfficeStatus(@RequestBody UpdateOfficeStatusRequest request){
        SingleResponse<?> response = adminService.updateOfficeStatus(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/getTodayAttendanceRecords")
    public ResponseEntity<SingleResponse<?>> getTodayAttendanceRecords(){
        SingleResponse<?> response = adminService.getTodayAttendanceRecords();
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/approval")
    public ResponseEntity<SingleResponse<?>> updateLeave(@RequestBody UpdateLeaveRequest request,
                                                         @RequestHeader("X-Employee-Id") String approvedEmployeeId){
        SingleResponse<?> response = adminService.updateLeave(request, approvedEmployeeId);
        return ResponseEntity.status(200).body(response);
    }
}
