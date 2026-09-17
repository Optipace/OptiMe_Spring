package com.employee.AuthService.dto.response;

import com.employee.AuthService.enums.IsDiscontinued;
import com.employee.AuthService.enums.UserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusGatewayResponse {
    private UserStatusEnum userStatus;
    private IsDiscontinued isDiscontinued;
}
