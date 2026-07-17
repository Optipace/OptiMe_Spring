package com.employee.AuthService.dto.response;

import com.employee.AuthService.enums.*;
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
    private List<EmployeeDesignationEnum> availableDesignations;
    private List<RoleEnum> roleEnumList;
    private List<WorkTypeEnum> workTypeEnumList;
    private List<EmployeeStatusEnum> employeeStatusEnumList;
    private List<LeaveTypeResponse> leaveTypeResponseList;
}
