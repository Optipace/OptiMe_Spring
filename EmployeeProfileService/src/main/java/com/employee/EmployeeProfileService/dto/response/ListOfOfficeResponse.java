package com.employee.EmployeeProfileService.dto.response;

import com.employee.EmployeeProfileService.enums.EmployeeDesignationEnum;
import com.employee.EmployeeProfileService.enums.EmployeeStatusEnum;
import com.employee.EmployeeProfileService.enums.RoleEnum;
import com.employee.EmployeeProfileService.enums.WorkTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListOfOfficeResponse {
//    private String OfficeId;
//    private String officeName;
//    private String latitude;
//    private String longitude;
//    private String hr;
//    private String address;
//    private String contact;
//    private String googleMap;
    private List<OfficeResponse> officeResponseList;
    private List<EmployeeDesignationEnum> availableDesignations;
    private List<RoleEnum> roleEnumList;
    private List<WorkTypeEnum> workTypeEnumList;
    private List<EmployeeStatusEnum> employeeStatusEnumList;
}
