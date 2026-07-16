package com.employee.EmployeeProfileService.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum CustomStatus {

    PASSWORD_UPDATED_SUCCESSFULLY(3,Series.CLIENT_ERROR,"PASSWORD_UPDATED_SUCCESSFULLY"),
    SUCCESSFULLY_LOGGED_OUT(2, Series.SUCCESSFUL,"SUCCESSFULLY_LOGGED_OUT"),
    SUCCESS(1, Series.SUCCESSFUL, "SUCCESS"),
    FAILURE(0, Series.CLIENT_ERROR, "FAILURE"),
    ALREDY_REGISTERED(-1,Series.CLIENT_ERROR,"ALREDY_REGISTERED"),
    MOBILE_NO_EXISTS(-2,Series.CLIENT_ERROR,"MOBILE_NO_EXISTS"),
    OTP_LIMITED(-3,Series.CLIENT_ERROR,"LIMIT_OVER"),
    INVALID_OTP(-4,Series.CLIENT_ERROR,"INVALID_OTP"),
    OTP_LIMIT_OVER(-5,Series.CLIENT_ERROR,"OTP_LIMIT_OVER"),
    EMAIL_NOT_FOUND(-6,Series.CLIENT_ERROR,"EMAIL_NOT_FOUND"),
    TOKEN_MISMATCH(-7,Series.CLIENT_ERROR,"TOKEN_MISMATCH"),
    USER_NOT_FOUND(-8,Series.CLIENT_ERROR,"USER_NOT_FOUND"),
    INVALIDTOKEN(-9,Series.CLIENT_ERROR,"INVALIDTOKEN"),
    TOKENEXPIRED(-10,Series.CLIENT_ERROR,"TOKENEXPIRED"),
    TOKENMISMATCH(-11,Series.CLIENT_ERROR,"TOKENMISMATCH"),
    STATE_NOT_FOUND(-12,Series.CLIENT_ERROR,"STATE_ID_NOT_FOUND"),
    CITY_NOT_FOUND(-13,Series.CLIENT_ERROR,"CITY_NOT_FOUND"),
    LOCATION_NOT_FOUND(-14,Series.CLIENT_ERROR,"LOCATION_NOT_FOUND"),
    ACADEMY_NOT_FOUND(-15,Series.CLIENT_ERROR,"ACADEMY_NOT_FOUND"),
    CONTACT_NOT_FOUND(-16,Series.CLIENT_ERROR,"CONTACT_NOT_FOUND"),
    EMAIL_ALREDY_FOUND(-17,Series.CLIENT_ERROR,"EMAIL_ALREDY_FOUND"),
    INVALID_CREDENTIAL(-18,Series.CLIENT_ERROR,"INVALID_CREDENTIAL"),
    INVALID_PASSWORD(-19,Series.CLIENT_ERROR,"INVALID_PASSWORD"),
    PLAYER_NOT_FOUND(-20,Series.CLIENT_ERROR,"PLAYER_NOT_FOUND"),
    LEVEL_NOT_FOUND(-21,Series.CLIENT_ERROR,"LEVEL_NOT_FOUND"),
    VIDEO_FILE_NOT_FOUND(-22,Series.CLIENT_ERROR,"VIDEO_FILE_NOT_FOUND"),
    VIDEO_NOT_FOUND(-23,Series.CLIENT_ERROR,"VIDEO_NOT_FOUND")
    ;
    private final int code;
    private final Series series;
    private final String message;

    private static final Map<Integer, CustomStatus> LOOKUP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(CustomStatus::getCode, Function.identity()));

    public static CustomStatus fromCode(int code) {
        return LOOKUP.getOrDefault(code, FAILURE);
    }

    @Getter
    @AllArgsConstructor
    public enum Series {
        INFORMATIONAL(1),
        SUCCESSFUL(2),
        REDIRECTION(3),
        CLIENT_ERROR(4),
        SERVER_ERROR(5);

        private final int value;
    }
}
