package com.employee.EmployeeProfileService.service;

import com.employee.EmployeeProfileService.dto.request.*;
import com.employee.EmployeeProfileService.dto.response.*;
import com.employee.EmployeeProfileService.model.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface EmployeeInternalService {

    public SingleResponse<?> createProfile(EmployeeProfileRequest request);

    public SingleResponse<Employee> completeProfile(CompleteProfileRequest request);

    SingleResponse<Long> getEmployeeByUserId(Long userId);

    public SingleResponse<EmployeeGetProfileResponse> getProfile(String employeeId);

    public SingleResponse<?> getMasterDetails();

    public boolean checkEmployeeByEmployeeId(Long employeeId);

    public SingleResponse<?> updateEmployeeAccountStatus(Long empId);

    public SingleResponse<EmployeeInternalResponse> getEmployeeById(Long employeeId);

    public SingleResponse<?> deleteIdentity(String employeeId);

    public SingleResponse<List<FeedbackResponse>> getFeedback();

    public SingleResponse<?> updateFeedback(FeedbackUpdateRequest request);

    public boolean getWorkTypeId(Long workTypeId);

    public SingleResponse<PageResponse<EmployeeResponse>> getAllEmployee(Pageable pageable);

    public SingleResponse<ListOfEmployeeIdResponse> getAllEmployeeId();

    public boolean isHrEmployeeId(Long hrEmpId);

    public SingleResponse<PageResponse<ListOfAdminInternalResponse>> getAllAdminDetails(Pageable pageable);

    public SingleResponse<?> getEmployeeName(Long employeeId);

    public SingleResponse<?> updateProfile(AdminUpdateEmployeeRequest request);

    public SingleResponse<Long> getEmployeeOfficeId(Long employeeId);

    public SingleResponse<List<EmployeeIdNameOfficeIdResponse>> getAllEmployeeIdAndName();

    public SingleResponse<?> updateEmployeeIsDisContinued(Long empId);
}
