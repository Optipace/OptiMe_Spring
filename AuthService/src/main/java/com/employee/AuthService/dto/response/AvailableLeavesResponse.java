package com.employee.AuthService.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvailableLeavesResponse {

    private Long id;

    private Long employeeId;

    private Integer remainingLeaves;
}
