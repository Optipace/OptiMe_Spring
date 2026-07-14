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
//    private String OfficeId;
//    private String officeName;
//    private String latitude;
//    private String longitude;
//    private String hr;
//    private String address;
//    private String contact;
//    private String googleMap;
    private List<OfficeResponse> OfficeResponse;
    private List<EmployeeDesignationEnum> availableDesignations;
    private List<RoleEnum> roleEnumList;
    private List<WorkTypeEnum> workTypeEnumList;
    private List<EmployeeStatusEnum> employeeStatusEnumList;
}
