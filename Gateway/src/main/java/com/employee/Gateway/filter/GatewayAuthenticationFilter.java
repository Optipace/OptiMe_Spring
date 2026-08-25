package com.employee.Gateway.filter;

import com.employee.Gateway.config.RouteValidator;
import com.employee.Gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class GatewayAuthenticationFilter extends AbstractGatewayFilterFactory<GatewayAuthenticationFilter.Config>{

    private final JwtUtil jwtUtil;
    private final RouteValidator validator;

    public GatewayAuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil){
        super(Config.class);
        this.jwtUtil = jwtUtil;
        this.validator = validator;
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 1. Check if the route is secured
            if(validator.isSecured.test(exchange.getRequest())){

                // 2. Check if the Authentication header is present
                if(!exchange.getRequest().getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)){
                    return handleUnauthorised(exchange);
                }

                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if(authHeader != null && authHeader.startsWith("Bearer ")){
                    authHeader = authHeader.substring(7);
                }else{
                    return handleUnauthorised(exchange);
                }
                try{
                    // 1. Validate token
                    if(!jwtUtil.validateToken(authHeader)){
                        log.error("JWT validation failed");
                        return  handleUnauthorised(exchange);
                    }

                    // 2. Extract employeeId and role
                    String employeeId = jwtUtil.extractEmployeeId(authHeader);
                    String role = jwtUtil.extractRole(authHeader);
                    String employeeName = jwtUtil.extractUsername(authHeader);
                    String emailId = jwtUtil.extractEmailId(authHeader);
                    String id = jwtUtil.extractId(authHeader);
                    String userId = jwtUtil.extractUserId(authHeader);

                    // --- ROLE-BASED AUTHORIZATION BLOCK ---
                    String path = exchange.getRequest().getURI().getPath();
                    if (path.startsWith("/api/admin")) {
                        if (!"ADMIN".equals(role)) {
                            // User is logged in, but not an admin! Boot them out.
                            return handleUnauthorised(exchange);
                        }
                    }
                    // 3. Mutate the request (Add the ID as a downstream header)
                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                    .header("X-Employee-Id",employeeId)
                                    .header("X-User-Role", role)
                                    .header("X-Employee-Name", employeeName)
                                    .header("X-Email-Id", emailId)
                                    .header("X-Id", id)
                                    .header("X-User-Id", userId)
                                    .build())
                            .build();

                    return chain.filter(mutatedExchange);
                }catch (Exception e){
                    log.error("CRITICAL JWT ERROR: " + e.getMessage());
                    e.printStackTrace();
                    return handleUnauthorised(exchange);
                }
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> handleUnauthorised(ServerWebExchange exchange){
//        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//        // You can customize the response body here if needed, but returning a 401 status is standard
//        return exchange.getResponse().setComplete();
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String jsonResponse = "{\"error\": \"Unauthorized\", \"message\": \"Invalid token or token expired.\"}";
        DataBuffer buffer = response.bufferFactory().wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {
        // Empty class as we don't need custom properties for the filter right now
    }
}

