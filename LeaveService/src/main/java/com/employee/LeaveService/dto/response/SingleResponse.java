package com.employee.LeaveService.dto.response;

import com.employee.LeaveService.enums.CustomStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SingleResponse<T> {
    private T data;
    private Response response;
    private int statusCode;
    private String message;
    private HttpStatus httpStatus;

    public SingleResponse(T data, CustomStatus status){
        this.data = data;
        this.response = new Response(status.getCode(), status.getMessage());
    }

    public SingleResponse(T data, CustomStatus status, int statusCode){
        this.data = data;
        this.statusCode = statusCode;
        this.response = new Response(status.getCode(), status.getMessage());
    }

    public SingleResponse(String message, HttpStatus httpStatus){
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @JsonIgnore
    public int getStatusCode() {
        return statusCode;
    }

    @JsonIgnore
    public String getMessage(){return message;}

    @JsonIgnore
    public HttpStatus getHttpStatus(){return httpStatus;}
}
