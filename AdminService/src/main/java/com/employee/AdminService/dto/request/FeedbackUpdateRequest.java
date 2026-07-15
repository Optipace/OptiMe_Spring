package com.employee.AdminService.dto.request;

import com.employee.AdminService.enums.FeedbackStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackUpdateRequest {
    private Long feedbackId;
    private FeedbackStatusEnum feedbackStatus;
}
