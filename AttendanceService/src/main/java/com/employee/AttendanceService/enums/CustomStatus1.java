package com.employee.AttendanceService.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomStatus1 {
    UNKNOWN(-999,"Something went wrong"),

    // 400 Bad Requests
    VALIDATION_FAILED(-72, "Validation failed."),
    INVALID_REQUEST_BODY(-71, "Invalid request body."),
    INVALID_EMAIL_ADDRESS(-58, "Invalid email address."),
    INVALID_LEAVE_DATE_RANGE(-48, "The 'To Date' cannot be earlier than the 'From Date'."),
    INVALID_LEAVE_STATUS(-46, "Invalid leave status."),
    INVALID_FEEDBACK_STATUS(-43, "Invalid feedback status."),
    INVALID_PROFILE_STATUS(-36, "Invalid profile status."),
    INVALID_WORK_TYPE(-17, "Invalid work type."),
    FILE_IS_EMPTY(-42, "File is empty."),
    INVALID_MOBILE_OTP(-25, "Invalid mobile OTP."),
    INVALID_EMAIL_OTP(-24, "Invalid email OTP."),
    INVALID_OTP(-23, "Both email and mobile OTPs are invalid."),
    OTP_EXPIRED(-21, "OTP has expired."),

    // 401 Unauthorised access
    JWT_VALIDATION_FAILED(-68, "JWT validation failed."),
    MISSING_AUTHORIZATION_HEADER(-67, "Authorization header is missing."),
    BEARER_TOKEN_REQUIRED(-66, "Bearer token is required in the Authorization header."),
    INVALID_TOKEN_FORMAT(-62, "Authorization header must start with 'Bearer '."),
    TOKEN_CLAIM_EXTRACTION_FAILED(-65, "Failed to extract token claims."),
    WEBSOCKET_AUTHENTICATION_FAILED(-63, "WebSocket authentication failed."),
    INVALID_PASSWORD(-32, "Invalid password."),
    INVALID_VALIDATION_TOKEN(-27, "Invalid validation token."),
    OTP_NOT_VALIDATED(-28, "OTP has not been validated."),

    // 403 Forbidden
    GATEWAY_AUTHORIZATION_FAILED(-70, "Authorization failed."),
    ADMIN_ACCESS_REQUIRED(-69, "Admin privileges are required to access this resource."),
    UNAUTHORIZED_LEAVE_APPROVER(-51, "You are not authorized to approve leave."),
    PROFILE_COMPLETION_REQUIRED(-35, "Profile completion is required."),
    WEBSOCKET_CONNECTION_REJECTED(-61, "WebSocket connection rejected."),

    // 404 Not Found
    USER_NOT_FOUND(-31, "User not found."),
    ADMIN_NOT_FOUND(-1, "Admin not found."),
    EMPLOYEE_NOT_FOUND(-19, "Employee not found."),
    EMPLOYEE_ID_NOT_FOUND(-26, "Employee ID not found."),
    EMPLOYEE_PROFILE_NOT_FOUND(-40, "Employee profile not found."),
    EMPLOYEE_STATUS_NOT_FOUND(-38, "Employee status not found."),
    APPROVER_NOT_FOUND(-45, "Approver not found."),
    LEAVE_NOT_FOUND(-49, "Leave not found."),
    LEAVE_TYPE_NOT_FOUND(-50, "Leave type not found."),
    NO_APPLIED_LEAVE_RECORDS_FOUND(-47, "No applied leave records found."),
    LEAVE_RECORDS_NOT_FOUND(-8, "No leave records found."),
    ATTENDANCE_STATUS_NOT_FOUND(-16, "Attendance status not found."),
    ATTENDANCE_RECORDS_NOT_FOUND(-15, "No attendance records found."),
    CHECK_IN_RECORD_NOT_FOUND(-14, "No active check-in record found."),
    FEEDBACK_NOT_FOUND(-7, "Feedback not found."),
    OFFICE_NOT_FOUND(-6, "Office not found."),
    NO_OFFICE_RECORDS_FOUND(-4, "No office records found."),
    ACTIVE_OFFICE_NOT_FOUND(-41, "No active office found."),
    EMAIL_TEMPLATE_NOT_FOUND(-56, "Email template not found."),
    EMAIL_ATTACHMENT_NOT_FOUND(-55, "Email attachment not found."), // <--47

    // 409 Conflict(e.g., duplicates, already processed)
    LEAVE_ALREADY_PROCESSED(-52, "Leave has already been processed."),
    PROFILE_ALREADY_COMPLETED(-44, "Profile is already completed."),
    FEEDBACK_ALREADY_PROCESSED(-39, "Feedback has already been processed."),
    EMPLOYEE_PROFILE_ALREADY_EXISTS(-37, "Employee profile already exists."),
    EMPLOYEE_ID_ALREADY_EXISTS(-33, "Employee ID already exists."),
    PERSONAL_EMAIL_ALREADY_EXISTS(-30, "Personal email already exists."),
    EMPLOYEE_ID_ALREADY_REGISTERED(-29, "Employee is already fully registered."),
    OTP_ALREADY_VERIFIED(-20, "OTP already verified."),
    ALREADY_CHECKED_IN(-13, "Employee is already checked in."),
    OFFICE_ALREADY_EXISTS(-5, "Office already exists."),
    OFFICE_INACTIVE(-3, "Office is inactive."),

    // 413 Payload Too Large
    IMAGE_SIZE_EXCEEDED(-12, "Image exceeds the maximum allowed size."),

    // 415 Unsupported Media Type
    INVALID_IMAGE_FORMAT(-11, "Only JPEG, JPG or PNG files are allowed."),

    // 429 Too Many Requests
    OTP_RETRY_LIMIT_EXCEEDED(-22, "OTP retry limit exceeded."),

    // 500 Internal Server Errors
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

    // 503 Service Unavailable
    SERVICE_UNAVAILABLE(-101, "Requested service is unavailable."),

    // 502 Bad Gateway
    MICROSERVICE_CALL_FAILED(-100, "Microservice call failed."),

    // Common Failures
    FAILURE(0,"Something went wrong."),

    SUCCESS(1, "SUCCESS"); // 79

    private final int code;
    private final String message;

}
