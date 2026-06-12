package com.employee.Gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    //Defining the open endpoints that DO NOT require a token
    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/getOtp",
            "/api/auth/validateOtp",
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
