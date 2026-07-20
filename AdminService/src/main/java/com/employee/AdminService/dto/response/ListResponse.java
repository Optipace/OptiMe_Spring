package com.employee.AdminService.dto.response;

import com.employee.AdminService.enums.CustomStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class ListResponse<T> {
    private List<T> data;
    private Response response;
    private int statusCode;

    public ListResponse(List<T> data, CustomStatus status){
        this.data = data;
        this.response = new Response(status.getCode(), status.getMessage());
    }

    public ListResponse(List<T> data, CustomStatus status, int statusCode){
        this.data = data;
        this.statusCode = statusCode;
        this.response = new Response(status.getCode(), status.getMessage());
    }

    @JsonIgnore
    public int getStatusCode() {
        return statusCode;
    }
}
