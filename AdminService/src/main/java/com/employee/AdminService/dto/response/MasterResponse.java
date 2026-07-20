package com.employee.AdminService.dto.response;

import com.employee.AdminService.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MasterResponse {
    private List<OfficeResponse> OfficeResponse;
    private List<LeaveTypeResponse> leaveTypeResponseList;
    private List<EmployeeDesignationResponse> availableDesignations;
    private List<RoleEnum> roleEnumList;
    private List<WorkTypeResponse> workTypeList;
    private List<EmployeeStatusResponse> employeeStatusList;
}
