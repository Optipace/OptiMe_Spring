package com.employee.Gateway.GatewayAuthenticationFilter;

import com.employee.Gateway.config.RouteValidator;
import com.employee.Gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config>{

    private final JwtUtil jwtUtil;
    private final RouteValidator validator;

    public  AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil){
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
                    jwtUtil.validateToken(authHeader);

                    // 2. Extract employeeId
                    String employeeId = jwtUtil.extractEmployeeId(authHeader);

                    // 3. Mutate the request (Add the ID as a downstream header)
                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                    .header("X-Employee-Id",employeeId)
                                    .build())
                            .build();

                    return chain.filter(mutatedExchange);
                }catch (Exception e){
                    return handleUnauthorised(exchange);
                }
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> handleUnauthorised(ServerWebExchange exchange){
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        // You can customize the response body here if needed, but returning a 401 status is standard
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Empty class as we don't need custom properties for the filter right now
    }
}

