package com.employee.Gateway.dto.response;


import com.employee.Gateway.enums.IsDiscontinued;
import com.employee.Gateway.enums.UserStatusEnum;
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
