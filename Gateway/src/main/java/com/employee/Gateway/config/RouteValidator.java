package com.employee.Gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
@Slf4j
public class RouteValidator {

    //Defining the open endpoints that DO NOT require a token
    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/getOtp",
            "/api/auth/validateOtp",
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/auth/completeRegistration",
            "/api/auth/resetPassword",
            "/api/admin/interview/submit"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request ->{
                String path = request.getURI().getPath();
                log.info("Path {}",path);
                //                    openApiEndpoints
//                    .stream()
//                    .noneMatch(uri -> request.getURI().getPath().equals(uri));
                boolean isBusinessOpenApi = openApiEndpoints.stream().anyMatch(path::equals);
                log.info("Is business Open api endpoint {}",isBusinessOpenApi);

                boolean isSwaggerPath = path.contains("/swagger-ui")
                        ||  path.contains("/v3/api-docs")
                        ||  path.contains("/webjars")
                        || path.contains("/swagger-resources");
                log.info("Is swagger path {}", isSwaggerPath);

                log.info("is Secured endpoint {}", !(isBusinessOpenApi || isSwaggerPath));
                return !(isBusinessOpenApi || isSwaggerPath);
            };

//    private final AntPathMatcher pathMatcher = new AntPathMatcher();
//
//    public Predicate<ServerHttpRequest> isSecured = request -> {
//        String path = request.getURI().getPath();
//        return openApiEndpoints.stream()
//                // 2. Matches path structures safely instead of checking if string contains text
//                .noneMatch(pattern -> pathMatcher.match(pattern, path));
//    };
}
