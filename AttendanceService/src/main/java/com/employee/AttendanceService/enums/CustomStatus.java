package com.employee.AttendanceService.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomStatus {
    ROLLBACK_FAILED(-102, "Rollback operation failed."),
    SERVICE_UNAVAILABLE(-101, "Requested service is unavailable."),
    MICROSERVICE_CALL_FAILED(-100, "Microservice call failed."),
    UNEXPECTED_ERROR(-103, "An unexpected error occurred."),
    VALIDATION_FAILED(-104, "Validation failed."),
    INVALID_REQUEST_BODY(-105, "Invalid request body."),

    //------GATEWAY SERVICE CODES
    GATEWAY_AUTHORIZATION_FAILED(-73, "Authorization failed."),
    ADMIN_ACCESS_REQUIRED(-72, "Admin privileges are required to access this resource."),
    JWT_VALIDATION_FAILED(-71, "JWT validation failed."),
    MISSING_AUTHORIZATION_HEADER(-70, "Authorization header is missing."),
    BEARER_TOKEN_REQUIRED(-69, "Bearer token is required in the Authorization header."),
    TOKEN_CLAIM_EXTRACTION_FAILED(-68, "Failed to extract token claims."),
    GATEWAY_REQUEST_MUTATION_FAILED(-67, "Failed to process gateway request."),

    //----NOTIFICATION SERVICE CODES
    WEBSOCKET_AUTHENTICATION_FAILED(-66, "WebSocket authentication failed."),
    INVALID_TOKEN_FORMAT(-65, "Authorization header must start with 'Bearer '."),
    WEBSOCKET_CONNECTION_REJECTED(-64, "WebSocket connection rejected."),
    WEBSOCKET_MESSAGE_DELIVERY_FAILED(-63, "Failed to deliver WebSocket message."),
    WEBSOCKET_CONFIGURATION_ERROR(-62, "WebSocket configuration error."),
    INVALID_EMAIL_ADDRESS(-61, "Invalid email address."),
    EMAIL_TEMPLATE_PROCESSING_FAILED(-60, "Failed to process email template."),
    EMAIL_TEMPLATE_NOT_FOUND(-59, "Email template not found."),
    EMAIL_ATTACHMENT_NOT_FOUND(-58, "Email attachment not found."),
    EMAIL_CONFIGURATION_ERROR(-57, "Email configuration error."),
    EMAIL_DELIVERY_FAILED(-56, "Failed to deliver email."),

    //-------LEAVE SERVICE CODES
    LEAVE_ALREADY_PROCESSED(-55, "Leave has already been processed."),
    UNAUTHORIZED_LEAVE_APPROVER(-54, "You are not authorized to approve leave."),
    LEAVE_TYPE_NOT_FOUND(-53, "Leave type not found."),
    LEAVE_NOT_FOUND(-52, "Leave not found."),
    INVALID_LEAVE_DATE_RANGE(-51, "The 'To Date' cannot be earlier than the 'From Date'."),
    NO_APPLIED_LEAVE_RECORDS_FOUND(-50, "No applied leave records found."),
    INVALID_LEAVE_STATUS(-49, "Invalid leave status."),
    APPROVER_NOT_FOUND(-48, "Approver not found."),

    //------EMPLOYEE PROFILE SERVICE CODES
    PROFILE_ALREADY_COMPLETED(-47, "Profile is already completed."),
    INVALID_FEEDBACK_STATUS(-46, "Invalid feedback status."),
    FILE_IS_EMPTY(-45, "File is empty."),
    ACTIVE_OFFICE_NOT_FOUND(-44, "No active office found."),
    EMPLOYEE_PROFILE_NOT_FOUND(-43, "Employee profile not found."),
    FEEDBACK_ALREADY_PROCESSED(-42, "Feedback has already been processed."),
    EMPLOYEE_STATUS_NOT_FOUND(-41, "Employee status not found."),
    EMPLOYEE_PROFILE_ALREADY_EXISTS(-40, "Employee profile already exists."),
    INVALID_PROFILE_STATUS(-39, "Invalid profile status."),
    PROFILE_COMPLETION_REQUIRED(-38, "Profile completion is required."),

    //------AUTH SERVICE CODES
    EMAIL_SENDING_FAILED(-37, "Failed to send email."),
    EMPLOYEE_ID_ALREADY_EXISTS(-36, "Employee ID already exists."),
    INVALID_PASSWORD(-35, "Invalid password."),
    USER_NOT_FOUND(-34, "User not found."),
    PERSONAL_EMAIL_ALREADY_EXISTS(-33, "Personal email already exists."),
    EMPLOYEE_ID_ALREADY_REGISTERED(-32, "Employee is already fully registered."),
    OTP_NOT_VALIDATED(-31, "OTP has not been validated."),
    INVALID_VALIDATION_TOKEN(-30, "Invalid validation token."),
    EMPLOYEE_ID_NOT_FOUND(-29, "Employee ID not found."),
    INVALID_MOBILE_OTP(-28, "Invalid mobile OTP."),
    INVALID_EMAIL_OTP(-27, "Invalid email OTP."),
    INVALID_OTP(-26, "Both email and mobile OTPs are invalid."),
    OTP_RETRY_LIMIT_EXCEEDED(-25, "OTP retry limit exceeded."),
    OTP_EXPIRED(-24, "OTP has expired."),
    OTP_ALREADY_VERIFIED(-23, "OTP already verified."),

    //-------ATTENDANCE SERVICE CODES
    EMPLOYEE_NOT_FOUND(-22, "Employee not found."),
    EMPLOYEE_STATUS_UPDATE_FAILED(-21, "Failed to update employee status."),
    INVALID_WORK_TYPE(-20, "Invalid work type."),
    ATTENDANCE_STATUS_NOT_FOUND(-19, "Attendance status not found."),
    ATTENDANCE_RECORDS_NOT_FOUND(-18, "No attendance records found."),
    CHECK_IN_RECORD_NOT_FOUND(-17, "No active check-in record found."),
    ALREADY_CHECKED_IN(-16, "Employee is already checked in."),
    IMAGE_SIZE_EXCEEDED(-15, "Image exceeds the maximum allowed size."),
    INVALID_IMAGE_FORMAT(-14, "Only JPEG, JPG or PNG files are allowed."),
    FILE_UPLOAD_FAILED(-13, "File upload failed."),

    //------ADMIN SERVICE CODES
    BROADCAST_DELIVERY_FAILED(-9, "Failed to send broadcast notification."),
    LEAVE_RECORDS_NOT_FOUND(-8, "No leave records found."),
    FEEDBACK_NOT_FOUND(-7, "Feedback not found."),
    OFFICE_NOT_FOUND(-6, "Office not found."),
    OFFICE_ALREADY_EXISTS(-5, "Office already exists."),
    NO_OFFICE_RECORDS_FOUND(-4, "No office records found."),
    OFFICE_INACTIVE(-3, "Office is inactive."),
    EMPLOYEE_CREATION_FAILED(-2, "Failed to create employee."),
    ADMIN_NOT_FOUND(-1, "Admin not found."),

    //-------FAILURE CODES

    //-------SUCCESS CODES------
    SUCCESS(1,"SUCCESS"),
    LOGIN_SUCCESS(2, "Login successful."),
    LOGOUT_SUCCESS(3, "Logout successful."),
    PASSWORD_RESET_SUCCESS(4, "Password reset successfully."),

    //-------AUTH SERVICE CODES
    OTP_SENT_SUCCESSFULLY(5,"OTP sent successfully to your registered email and mobile number."),
    OTP_VALIDATED_SUCCESSFULLY(6,"OTP validated successfully."),
    REGISTRATION_COMPLETED(7,"Your account has been registered successfully."),
    PASSWORD_REST_SUCCESS(8,"Password reset successfully."),
    IDENTITY_CREATED(9, "Identity created successfully."),
    IDENTITY_DELETED(10, "Identity deleted successfully."),

    //-------ATTENDANCE SERVICE CODES
    CHECK_IN_SUCCESS(11,"Checked in successfully."),
    CHECK_OUT_SUCCESS(12,"Checked out successfully."),
    WORKING_DETAILS_FETCHED(13, "Working details fetched successfully."),
    WEEKLY_ATTENDANCE_FETCHED(14, "Weekly attendance records fetched successfully."),
    TODAY_ATTENDANCE_FETCHED(15, "Today's attendance records fetched successfully."),
    MONTHLY_ATTENDANCE_FETCHED(16, "Monthly attendance records fetched successfully."),
    ATTENDANCE_UPDATED(17, "Attendance updated successfully."),
    ATTENDANCE_STATUS_FETCHED(18, "Attendance status fetched successfully."),
    ATTENDANCE_LOGS_FETCHED(19, "Attendance logs fetched successfully."),

    //--------EMPLOYEE SERVICE CODES
    EMPLOYEE_LIST_FETCHED(20, "Employee list fetched successfully."),
    EMPLOYEE_DETAILS_FETCHED(21, "Employee details fetched successfully."),
    OFFICE_NAMES_FETCHED(22, "Office names fetched successfully."),
    PROFILE_PICTURE_UPLOADED(23, "Profile picture uploaded successfully."),
    PROFILE_FETCHED(24, "Employee profile fetched successfully."),
    FEEDBACK_SUBMITTED(25, "Feedback submitted successfully."),
    FEEDBACK_LIST_FETCHED(26, "Feedback list fetched successfully."),
    FEEDBACK_UPDATED(27, "Feedback updated successfully."),
    PROFILE_CREATED(28, "Employee profile created successfully."),
    PROFILE_COMPLETED(29, "Employee profile completed successfully."),
    PROFILE_STATUS_UPDATED(30, "Employee profile status updated successfully."),
    PROFILE_ROLLBACK_COMPLETED(31, "Employee profile rollback completed successfully."),

    //---------LEAVE SERVICE CODES
    LEAVE_APPLIED_SUCCESSFULLY(32,"Leave application submitted successfully."),
    LEAVE_APPROVED(33,"Leave approved successfully."),
    LEAVE_REJECTED(34,"Leave rejected successfully."),
    LEAVE_CANCELLED(35,"Leave cancelled successfully."),
    LEAVE_DETAILS_FETCHED(36, "Leave details fetched successfully."),
    LEAVE_LIST_FETCHED(37, "Leave records fetched successfully."),
    LEAVE_STATUS_UPDATED(38, "Leave status updated successfully."),
    LEAVE_TYPES_FETCHED(39, "Leave types fetched successfully."),


    //---------ADMIN SERVICE CODES
    EMPLOYEE_REGISTERED(40, "Employee registered successfully."),
    OFFICE_CREATED(41, "Office created successfully."),
    OFFICE_UPDATED(42, "Office details updated successfully."),
    OFFICE_LIST_FETCHED(43, "Office list fetched successfully."),
    APPLIED_LEAVE_LIST_FETCHED(44, "Applied leave records fetched successfully."),
    BROADCAST_NOTIFICATION_SENT(45, "Broadcast message sent successfully."),

    MASTER_DETAILS_FETCHED(50, "Master details fetched successfully.");

    private final int code;
    private final String message;
}
