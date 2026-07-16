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
//                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
//                   CHANGED from .contains(uri) to .equals(uri)
                    .noneMatch(uri -> request.getURI().getPath().equals(uri));

//    TODO: It's skipping all url with /api/auth
//public Predicate<ServerHttpRequest> isSecured =
//        request -> openApiEndpoints
//                .stream()
//                .noneMatch(uri -> {
//                    String incomingPath = request.getURI().getPath();
//                    // Remove trailing slash if it exists
//                    if (incomingPath.endsWith("/")) {
//                        incomingPath = incomingPath.substring(0, incomingPath.length() - 1);
//                    }
//                    return incomingPath.equals(uri);
//                });

//    private final AntPathMatcher pathMatcher = new AntPathMatcher();
//
//    public Predicate<ServerHttpRequest> isSecured = request -> {
//        String path = request.getURI().getPath();
//        return openApiEndpoints.stream()
//                // 2. Matches path structures safely instead of checking if string contains text
//                .noneMatch(pattern -> pathMatcher.match(pattern, path));
//    };

    // Checks if the incoming request path matches any of our public patterns exactly
//    public Predicate<ServerHttpRequest> isSecured = request -> {
//        String path = request.getURI().getPath();
//        return openApiEndpoints.stream()
//                .noneMatch(uri -> path.equals(uri)); // Exact matching ensures safety
//    };

}
