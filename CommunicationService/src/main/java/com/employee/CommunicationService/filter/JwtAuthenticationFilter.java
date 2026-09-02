//package com.employee.CommunicationService.filter;
//
//import com.employee.CommunicationService.util.JwtUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter implements WebFilter {
//
//    private final JwtUtil jwtUtil;
//
//    @Override
//    public Mono<Void> filter(
//            ServerWebExchange exchange,
//            WebFilterChain chain) {
//
//        String path=exchange.getRequest()
//                .getPath()
//                .value();
//        // Don't require JWT for internal endpoints
//        if (path.startsWith("/api/notifications/internal")||path.startsWith("/api/communication/email")) {
//            return chain.filter(exchange);
//        }
//        String authHeader = exchange.getRequest()
//                .getHeaders()
//                .getFirst(HttpHeaders.AUTHORIZATION);
//
//        // No JWT -> let Spring Security handle authentication
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return chain.filter(exchange);
//        }
//
//        String token = authHeader.substring(7);
//
//        try {
//
//            // Validate JWT
//            if (!jwtUtil.validateToken(token)) {
//                exchange.getResponse()
//                        .setStatusCode(HttpStatus.UNAUTHORIZED);
//
//                return exchange.getResponse().setComplete();
//            }
//
//            String username = jwtUtil.extractUsername(token);
//            String role = jwtUtil.extractRole(token);
//
//            List<GrantedAuthority> authorities =
//                    List.of(
//                            new SimpleGrantedAuthority("ROLE_" + role)
//                    );
//
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(
//                            username,
//                            null,
//                            authorities
//                    );
//
//            return chain.filter(exchange)
//                    .contextWrite(
//                            ReactiveSecurityContextHolder.withAuthentication(
//                                    authentication
//                            )
//                    );
//
//        } catch (Exception e) {
//
//            exchange.getResponse()
//                    .setStatusCode(HttpStatus.UNAUTHORIZED);
//
//            return exchange.getResponse().setComplete();
//        }
//    }
//}
