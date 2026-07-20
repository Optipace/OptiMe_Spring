package com.employee.AttendanceService.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomStatus {
    UNKNOWN(-999,"Unknown things happened"),
    // INTERNAL SERVICE CODES
    ROLLBACK_FAILED(-102, "Rollback operation failed."),
    SERVICE_UNAVAILABLE(-101, "Requested service is unavailable."),
    MICROSERVICE_CALL_FAILED(-100, "Microservice call failed."),

    UNEXPECTED_ERROR(-73, "An unexpected error occurred."),
    VALIDATION_FAILED(-72, "Validation failed."),
    INVALID_REQUEST_BODY(-71, "Invalid request body."),

    //------GATEWAY SERVICE CODES
    GATEWAY_AUTHORIZATION_FAILED(-70, "Authorization failed."),
    ADMIN_ACCESS_REQUIRED(-69, "Admin privileges are required to access this resource."),
    JWT_VALIDATION_FAILED(-68, "JWT validation failed."),
    MISSING_AUTHORIZATION_HEADER(-67, "Authorization header is missing."),
    BEARER_TOKEN_REQUIRED(-66, "Bearer token is required in the Authorization header."),
    TOKEN_CLAIM_EXTRACTION_FAILED(-65, "Failed to extract token claims."),
    GATEWAY_REQUEST_MUTATION_FAILED(-64, "Failed to process gateway request."),

    //----NOTIFICATION SERVICE CODES
    WEBSOCKET_AUTHENTICATION_FAILED(-63, "WebSocket authentication failed."),
    INVALID_TOKEN_FORMAT(-62, "Authorization header must start with 'Bearer '."),
    WEBSOCKET_CONNECTION_REJECTED(-61, "WebSocket connection rejected."),
    WEBSOCKET_MESSAGE_DELIVERY_FAILED(-60, "Failed to deliver WebSocket message."),
    WEBSOCKET_CONFIGURATION_ERROR(-59, "WebSocket configuration error."),
    INVALID_EMAIL_ADDRESS(-58, "Invalid email address."),
    EMAIL_TEMPLATE_PROCESSING_FAILED(-57, "Failed to process email template."), // <--
    EMAIL_TEMPLATE_NOT_FOUND(-56, "Email template not found."), // <--
    EMAIL_ATTACHMENT_NOT_FOUND(-55, "Email attachment not found."), // <--
    EMAIL_CONFIGURATION_ERROR(-54, "Email configuration error."),  // <--
    EMAIL_DELIVERY_FAILED(-53, "Failed to deliver email."), // <--

    //-------LEAVE SERVICE CODES
    LEAVE_ALREADY_PROCESSED(-52, "Leave has already been processed."),
    UNAUTHORIZED_LEAVE_APPROVER(-51, "You are not authorized to approve leave."),
    LEAVE_TYPE_NOT_FOUND(-50, "Leave type not found."),
    LEAVE_NOT_FOUND(-49, "Leave not found."),
    INVALID_LEAVE_DATE_RANGE(-48, "The 'To Date' cannot be earlier than the 'From Date'."),
    NO_APPLIED_LEAVE_RECORDS_FOUND(-47, "No applied leave records found."),
    INVALID_LEAVE_STATUS(-46, "Invalid leave status."),
    APPROVER_NOT_FOUND(-45, "Approver not found."),

    //------EMPLOYEE PROFILE SERVICE CODES
    PROFILE_ALREADY_COMPLETED(-44, "Profile is already completed."),
    INVALID_FEEDBACK_STATUS(-43, "Invalid feedback status."),
    FILE_IS_EMPTY(-42, "File is empty."),
    ACTIVE_OFFICE_NOT_FOUND(-41, "No active office found."),
    EMPLOYEE_PROFILE_NOT_FOUND(-40, "Employee profile not found."),
    FEEDBACK_ALREADY_PROCESSED(-39, "Feedback has already been processed."),
    EMPLOYEE_STATUS_NOT_FOUND(-38, "Employee status not found."),
    EMPLOYEE_PROFILE_ALREADY_EXISTS(-37, "Employee profile already exists."),
    INVALID_PROFILE_STATUS(-36, "Invalid profile status."),
    PROFILE_COMPLETION_REQUIRED(-35, "Profile completion is required."),

    //------AUTH SERVICE CODES
    EMAIL_SENDING_FAILED(-34, "Failed to send email."), // <--
    EMPLOYEE_ID_ALREADY_EXISTS(-33, "Employee ID already exists."),
    INVALID_PASSWORD(-32, "Invalid password."),
    USER_NOT_FOUND(-31, "User not found."),
    PERSONAL_EMAIL_ALREADY_EXISTS(-30, "Personal email already exists."),
    EMPLOYEE_ID_ALREADY_REGISTERED(-29, "Employee is already fully registered."),
    OTP_NOT_VALIDATED(-28, "OTP has not been validated."),
    INVALID_VALIDATION_TOKEN(-27, "Invalid validation token."),
    EMPLOYEE_ID_NOT_FOUND(-26, "Employee ID not found."),
    INVALID_MOBILE_OTP(-25, "Invalid mobile OTP."),
    INVALID_EMAIL_OTP(-24, "Invalid email OTP."),
    INVALID_OTP(-23, "Both email and mobile OTPs are invalid."),
    OTP_RETRY_LIMIT_EXCEEDED(-22, "OTP retry limit exceeded."),
    OTP_EXPIRED(-21, "OTP has expired."),
    OTP_ALREADY_VERIFIED(-20, "OTP already verified."),

    //-------ATTENDANCE SERVICE CODES
    EMPLOYEE_NOT_FOUND(-19, "Employee not found."),
    EMPLOYEE_STATUS_UPDATE_FAILED(-18, "Failed to update employee status."), // <--
    INVALID_WORK_TYPE(-17, "Invalid work type."),
    ATTENDANCE_STATUS_NOT_FOUND(-16, "Attendance status not found."),
    ATTENDANCE_RECORDS_NOT_FOUND(-15, "No attendance records found."),
    CHECK_IN_RECORD_NOT_FOUND(-14, "No active check-in record found."),
    ALREADY_CHECKED_IN(-13, "Employee is already checked in."),
    IMAGE_SIZE_EXCEEDED(-12, "Image exceeds the maximum allowed size."),
    INVALID_IMAGE_FORMAT(-11, "Only JPEG, JPG or PNG files are allowed."),
    FILE_UPLOAD_FAILED(-10, "File upload failed."),

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

    FAILURE(0,"Something went wrong."), // Don't know what happened for both server and frontend

    //-------FAILURE CODES

    //-------SUCCESS CODES------
    SUCCESS(1,"SUCCESS"); // 79
//    LOGIN_SUCCESS(2, "Login successful."),
//    LOGOUT_SUCCESS(3, "Logout successful."),
//    PASSWORD_RESET_SUCCESS(4, "Password reset successfully."),

    //-------AUTH SERVICE CODES
//    OTP_SENT_SUCCESSFULLY(5,"OTP sent successfully to your registered email and mobile number."),
//    OTP_VALIDATED_SUCCESSFULLY(6,"OTP validated successfully."),
//    REGISTRATION_COMPLETED(7,"Your account has been registered successfully."),
//    PASSWORD_REST_SUCCESS(8,"Password reset successfully."),
//    IDENTITY_CREATED(9, "Identity created successfully."),
//    IDENTITY_DELETED(10, "Identity deleted successfully."),

    //-------ATTENDANCE SERVICE CODES
//    CHECK_IN_SUCCESS(11,"Checked in successfully."),
//    CHECK_OUT_SUCCESS(12,"Checked out successfully."),
//    WORKING_DETAILS_FETCHED(13, "Working details fetched successfully."),
//    WEEKLY_ATTENDANCE_FETCHED(14, "Weekly attendance records fetched successfully."),
//    TODAY_ATTENDANCE_FETCHED(15, "Today's attendance records fetched successfully."),
//    MONTHLY_ATTENDANCE_FETCHED(16, "Monthly attendance records fetched successfully."),
//    ATTENDANCE_UPDATED(17, "Attendance updated successfully."),
//    ATTENDANCE_STATUS_FETCHED(18, "Attendance status fetched successfully."),
//    ATTENDANCE_LOGS_FETCHED(19, "Attendance logs fetched successfully."),

    //--------EMPLOYEE SERVICE CODES
//    EMPLOYEE_LIST_FETCHED(20, "Employee list fetched successfully."),
//    EMPLOYEE_DETAILS_FETCHED(21, "Employee details fetched successfully."),
//    OFFICE_NAMES_FETCHED(22, "Office names fetched successfully."),
//    PROFILE_PICTURE_UPLOADED(23, "Profile picture uploaded successfully."),
//    PROFILE_FETCHED(24, "Employee profile fetched successfully."),
//    FEEDBACK_SUBMITTED(25, "Feedback submitted successfully."),
//    FEEDBACK_LIST_FETCHED(26, "Feedback list fetched successfully."),
//    FEEDBACK_UPDATED(27, "Feedback updated successfully."),
//    PROFILE_CREATED(28, "Employee profile created successfully."),
//    PROFILE_COMPLETED(29, "Employee profile completed successfully."),
//    PROFILE_STATUS_UPDATED(30, "Employee profile status updated successfully."),
//    PROFILE_ROLLBACK_COMPLETED(31, "Employee profile rollback completed successfully."),

    //---------LEAVE SERVICE CODES
//    LEAVE_APPLIED_SUCCESSFULLY(32,"Leave application submitted successfully."),
//    LEAVE_APPROVED(33,"Leave approved successfully."),
//    LEAVE_REJECTED(34,"Leave rejected successfully."),
//    LEAVE_CANCELLED(35,"Leave cancelled successfully."),
//    LEAVE_DETAILS_FETCHED(36, "Leave details fetched successfully."),
//    LEAVE_LIST_FETCHED(37, "Leave records fetched successfully."),
//    LEAVE_STATUS_UPDATED(38, "Leave status updated successfully."),
//    LEAVE_TYPES_FETCHED(39, "Leave types fetched successfully."),


    //---------ADMIN SERVICE CODES
//    EMPLOYEE_REGISTERED(40, "Employee registered successfully."),
//    OFFICE_CREATED(41, "Office created successfully."),
//    OFFICE_UPDATED(42, "Office details updated successfully."),
//    OFFICE_LIST_FETCHED(43, "Office list fetched successfully."),
//    APPLIED_LEAVE_LIST_FETCHED(44, "Applied leave records fetched successfully."),
//    BROADCAST_NOTIFICATION_SENT(45, "Broadcast message sent successfully."),

//    MASTER_DETAILS_FETCHED(50, "Master details fetched successfully.");

    private final int code;
    private final String message;
}
