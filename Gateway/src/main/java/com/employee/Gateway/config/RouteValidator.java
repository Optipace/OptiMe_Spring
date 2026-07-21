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
            "/api/auth/refresh",
            "/api/auth/completeRegistration",
            "/api/auth/resetPassword"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().equals(uri));
//
//    String path = request.getURI().getPath();
//
//    boolean isBusinessOpenApi = openApiEndpoints.stream().anyMatch(path::equals);
//
//    boolean isSwaggerPath = path.contains("/swagger-ui")
//            ||  path.contains("/v3/api-docs")
//            ||  path.contains("/webjars")
//            || path.contains("/swagger-resources");
//
//        return !(isBusinessOpenApi || isSwaggerPath);

//    private final AntPathMatcher pathMatcher = new AntPathMatcher();
//
//    public Predicate<ServerHttpRequest> isSecured = request -> {
//        String path = request.getURI().getPath();
//        return openApiEndpoints.stream()
//                // 2. Matches path structures safely instead of checking if string contains text
//                .noneMatch(pattern -> pathMatcher.match(pattern, path));
//    };
}
