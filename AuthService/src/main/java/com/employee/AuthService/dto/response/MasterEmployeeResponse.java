package com.employee.AuthService.dto.response;

import com.employee.AuthService.enums.RoleEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MasterEmployeeResponse {
    private List<EmployeeDesignationResponse> availableDesignationsList;
    private List<RoleEnum> roleEnumList;
    private List<WorkTypeResponse> workTypeList;
    private List<EmployeeStatusResponse> employeeStatusList;
}
