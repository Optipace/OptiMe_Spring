package com.employee.AttendanceService.dto.response;

import com.employee.AttendanceService.enums.CustomStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SingleResponse<T> {
    private T data;
    private Response response;
    private int statusCode;

    public SingleResponse(T data, CustomStatus status){
        this.data = data;
        this.response = new Response(status.getCode(), status.getMessage());
    }

    public SingleResponse(T data, CustomStatus status, int statusCode){
        this.data = data;
        this.statusCode = statusCode;
        this.response = new Response(status.getCode(), status.getMessage());
    }

    @JsonIgnore
    public int getStatusCode() {
        return statusCode;
    }
}
