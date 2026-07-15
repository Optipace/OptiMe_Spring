package com.employee.AdminService.dto.response;

import com.employee.AdminService.enums.FeedbackStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {
    private Long feedbackId;
    private String employeeName;
    private String feedback;
    private FeedbackStatusEnum feedbackStatus;
}
