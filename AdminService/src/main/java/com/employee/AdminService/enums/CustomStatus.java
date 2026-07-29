package com.employee.AdminService.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomStatus {
    UNKNOWN(-999,"Something went wrong"),
// Highest -80
    // 200 Business Logic Failures
    INVALID_REQUEST_FORMAT(-105, "Invalid request format or Invalid body"),
    // 201: Authentication & OTP Failures
    HR_EMP_ID_NOT_FOUND(-79, "The provided employee id is not HR"),
    SAME_AS_OLD_PASSWORD(-78, "New password cannot be the same as the old password."),
    INVALID_REFRESH_TOKEN(-76, "Refresh token is invalid."),
    REFRESH_TOKEN_EXPIRED(-77, "Token expired login again."),
    IDENTITY_NOT_FOUND(-75, "Validated Email-Id or contact not found OR check provided employee Id"),
    NOT_REGISTERED(-74, "Please register this email or contact number in office!."),
    OTP_NOT_FOUND(-75, "OTP not found for this user."),
    OTP_ALREADY_VERIFIED(-20, "OTP already verified."),
    OTP_EXPIRED(-21, "OTP has expired."),
    OTP_RETRY_LIMIT_EXCEEDED(-22, "OTP retry limit exceeded."),
    INVALID_OTP(-23, "Both email and mobile OTPs are invalid."),
    INVALID_EMAIL_OTP(-24, "Invalid email OTP."),
    INVALID_MOBILE_OTP(-25, "Invalid mobile OTP."),
    INVALID_VALIDATION_TOKEN(-27, "Invalid validation token."),
    OTP_NOT_VALIDATED(-28, "OTP has not been validated."),
    INVALID_PASSWORD(-32, "Invalid password."),

    // 202: User Identity & Profile Failures
    ADMIN_NOT_FOUND(-1, "Admin not found."),
    EMPLOYEE_NOT_FOUND(-19, "Employee not found."),
    EMPLOYEE_ID_NOT_FOUND(-26, "Employee ID not found."),
    EMPLOYEE_ID_ALREADY_REGISTERED(-29, "Employee is already fully registered."),
    PERSONAL_EMAIL_ALREADY_EXISTS(-30, "Personal email already exists."),
    USER_NOT_FOUND(-31, "User not found."),
    EMPLOYEE_ID_ALREADY_EXISTS(-33, "Employee ID already exists."),
    PROFILE_COMPLETION_REQUIRED(-35, "Profile completion is required."),
    INVALID_PROFILE_STATUS(-36, "Invalid profile status."),
    EMPLOYEE_PROFILE_ALREADY_EXISTS(-37, "Employee profile already exists."),
    EMPLOYEE_PROFILE_NOT_FOUND(-40, "Employee profile not found."),
    PROFILE_ALREADY_COMPLETED(-44, "Profile is already completed."),

    // 203: Leave Management Failures
    DUPLICATE_LEAVE_APPLICATION(-80, "You have already applied for leave during this date range."),
    LEAVE_RECORDS_NOT_FOUND(-8, "No leave records found."),
    APPROVER_NOT_FOUND(-45, "Approver not found."),
    INVALID_LEAVE_STATUS(-46, "Invalid leave status."),
    NO_APPLIED_LEAVE_RECORDS_FOUND(-47, "No applied leave records found."),
    INVALID_LEAVE_DATE_RANGE(-48, "The 'To Date' cannot be earlier than the 'From Date'."),
    LEAVE_ID_NOT_FOUND(-49, "Leave Id not found."),
    LEAVE_TYPE_NOT_FOUND(-50, "Leave type not found."),
    UNAUTHORIZED_LEAVE_APPROVER(-51, "You are not authorized to approve leave."),
    LEAVE_ALREADY_PROCESSED(-52, "Leave has already been processed."),


    // 206: Attendance & Office Failures
    OFFICE_INACTIVE(-3, "Office is inactive."),
    NO_OFFICE_RECORDS_FOUND(-4, "No office records found."),
    OFFICE_ALREADY_EXISTS(-5, "Office already exists."),
    OFFICE_NOT_FOUND(-6, "Office not found."),
    ALREADY_CHECKED_IN(-13, "Employee is already checked in."),
    CHECK_IN_RECORD_NOT_FOUND(-14, "No active check-in record found."),
    ATTENDANCE_RECORDS_NOT_FOUND(-15, "No attendance records found."),
    ATTENDANCE_STATUS_NOT_FOUND(-16, "Attendance status not found."),
    INVALID_WORK_TYPE(-17, "Invalid work type."),
    ACTIVE_OFFICE_NOT_FOUND(-41, "No active office found."),

    // 207: Communications & Feedback Failures
    FEEDBACK_NOT_FOUND(-7, "Feedback not found."),
    FEEDBACK_ALREADY_PROCESSED(-39, "Feedback has already been processed."),
    INVALID_FEEDBACK_STATUS(-43, "Invalid feedback status."),
    EMAIL_ATTACHMENT_NOT_FOUND(-55, "Email attachment not found."),
    EMAIL_TEMPLATE_NOT_FOUND(-56, "Email template not found."),

    // 400 Series
    VALIDATION_FAILED(-72, "Validation failed."),
    INVALID_REQUEST_BODY(-71, "Invalid request body."),
    INVALID_EMAIL_ADDRESS(-58, "Invalid email address."),
    FILE_IS_EMPTY(-42, "File is empty."),
    MISSING_AUTHORIZATION_HEADER(-67, "Authorization header is missing."),
    BEARER_TOKEN_REQUIRED(-66, "Bearer token is required in the Authorization header."),
    INVALID_TOKEN_FORMAT(-62, "Authorization header must start with 'Bearer '."),
    JWT_VALIDATION_FAILED(-68, "JWT validation failed."),
    TOKEN_CLAIM_EXTRACTION_FAILED(-65, "Failed to extract token claims."),
    WEBSOCKET_AUTHENTICATION_FAILED(-63, "WebSocket authentication failed."),
    GATEWAY_AUTHORIZATION_FAILED(-70, "Authorization failed."),
    ADMIN_ACCESS_REQUIRED(-69, "Admin privileges are required to access this resource."),
    WEBSOCKET_CONNECTION_REJECTED(-61, "WebSocket connection rejected."),
    IMAGE_SIZE_EXCEEDED(-12, "Image exceeds the maximum allowed size."),
    INVALID_IMAGE_FORMAT(-11, "Only JPEG, JPG or PNG files are allowed."),

    // 500 Series
    UNEXPECTED_ERROR(-73, "An unexpected error occurred."),
    ROLLBACK_FAILED(-102, "Rollback operation failed."),
    GATEWAY_REQUEST_MUTATION_FAILED(-64, "Failed to process gateway request."),
    WEBSOCKET_MESSAGE_DELIVERY_FAILED(-60, "Failed to deliver WebSocket message."),
    WEBSOCKET_CONFIGURATION_ERROR(-59, "WebSocket configuration error."),
    EMAIL_TEMPLATE_PROCESSING_FAILED(-57, "Failed to process email template."),
    EMAIL_CONFIGURATION_ERROR(-54, "Email configuration error."),
    EMAIL_DELIVERY_FAILED(-53, "Failed to deliver email."),
    EMAIL_SENDING_FAILED(-34, "Failed to send email."),
    EMPLOYEE_STATUS_UPDATE_FAILED(-18, "Failed to update employee status."),
    EMPLOYEE_CREATION_FAILED(-2, "Failed to create employee."),
    FILE_UPLOAD_FAILED(-10, "File upload failed."),
    BROADCAST_DELIVERY_FAILED(-9, "Failed to send broadcast notification."),

    // 503
    SERVICE_UNAVAILABLE(-101, "Requested service is unavailable."),
    // 502
    MICROSERVICE_CALL_FAILED(-100, "Microservice call failed."),

    // Common Failures
    FAILURE(0,"Something went wrong."),

    SUCCESS(1, "SUCCESS");

    private final int code;
    private final String message;
}
