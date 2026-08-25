package com.employee.AdminService.service;

import com.employee.AdminService.dto.request.*;
import com.employee.AdminService.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    public SingleResponse<?> addNewUser(RegisterRequest request, String adminEmployeeId);

    public SingleResponse<PageResponse<EmployeeResponse>> getAllEmployee(Pageable pageable);

    public SingleResponse<?> addNewOffice(AddNewOfficeRequest request);

    public SingleResponse<PageResponse<OfficeResponse>> getOfficeList(Pageable pageable);

    public SingleResponse<?> updateOffice(UpdateOfficeRequest request);

    public SingleResponse<PageResponse<String>> getOfficeNames(Pageable pageable);

    public SingleResponse<List<FeedbackResponse>> getFeedback();

    public SingleResponse<?> updateFeedback(FeedbackUpdateRequest request);

    public SingleResponse<?> getAllAppliedLeaves();

    public SingleResponse<?> sendBroadcastMessage(NotificationRequest request);

    public SingleResponse<MasterResponse> getMasterDetails();

    public SingleResponse<?> updateOfficeStatus(UpdateOfficeStatusRequest request);

    public SingleResponse<?> getTodayAttendanceRecords();

//    public SingleResponse<?> updateLeave(UpdateLeaveRequest request, String approvedEmployeeId);

    SingleResponse<?> approveLeave(ApproveLeaveRequest request, String authorityEmployeeId);

    SingleResponse<?> rejectLeave(RejectLeaveRequest request, String authorityEmployeeId);

    public SingleResponse<PageResponse<AdminResponse>> getAllAdmin(Pageable pageable);

    SingleResponse<List<EmployeeAttendanceHistoryResponse>> getDateWiseAttendanceRecords(DateWiseAttendanceRequest request);

    SingleResponse<?> getWeeklyAttendanceLogs(Long employeeId);

    SingleResponse<?> updateCheckoutRecordByEmpId(UpdateCheckOutRecordsRequest request);

    SingleResponse<?> updateEmployee(UpdateEmployeeRequest request);
}
