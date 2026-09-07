package com.employee.AttendanceService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeIdNameOfficeIdResponse {
    private Long id;
    private String employeeName;
    private Long officeId;

    @Override
    public String toString() {
        return "EmployeeIdNameOfficeIdResponse{" +
                "id=" + id +
                ", employeeName='" + employeeName + '\'' +
                ", officeId='" + officeId + '\'' +
                '}';
    }
}
