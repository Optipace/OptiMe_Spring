package com.employee.Gateway.client;

import com.employee.Gateway.config.FeignConfig;
import com.employee.Gateway.dto.response.UserStatusGatewayResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "AUTH-SERVICE", configuration = FeignConfig.class)
public interface AuthClient {

    @GetMapping("/api/auth/internal/userStatus/{id}")
    public UserStatusGatewayResponse getUserStatus(@PathVariable Long id);
}
