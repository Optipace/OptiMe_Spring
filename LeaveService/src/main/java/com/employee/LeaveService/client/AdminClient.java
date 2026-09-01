package com.employee.LeaveService.client;

import com.employee.LeaveService.dto.response.OfficeResponse;
import com.employee.LeaveService.dto.response.SingleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ADMIN-SERVICE")
public interface AdminClient {
    @GetMapping("/api/admin/internal/getOfficeByOfficeId")
    SingleResponse<OfficeResponse> getOfficeDetails(@RequestParam("officeId") Long officeId);
}
