package com.employee.NotificationService.interceptor;

import com.employee.NotificationService.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Initial CONNECT frame
        if(StompCommand.CONNECT.equals(accessor.getCommand())){
            // Extract the Authorization header from the STOMP payload
            List<String> authorization = accessor.getNativeHeader("Authorization");

            if(authorization != null && !authorization.isEmpty()){
                String bearerToken = authorization.get(0);
                if(bearerToken.startsWith("Bearer ")){
                    String token = bearerToken.substring(7);

                    try{
                        jwtUtil.validateToken(token);

                        // Extract the employeeId and officially register it to this WebSocket session
                        String employeeId = jwtUtil.extractEmployeeId(token);

                        accessor.setUser(new Principal() {
                            @Override
                            public String getName() {
                                return employeeId;
                            }
                        });
                        log.info("WebSocket Authenticated for: "+employeeId);
                    }catch (Exception e){
                        log.error("WebSocket Connection rejected: Invalid JWT token");
                        throw new IllegalArgumentException("Invalid token");
                    }
                }else{
                    log.error("WebSocket Connection rejected: Missing Authorization header");
                    throw new IllegalArgumentException("No Token provided");
                }
            }
            return message; // Let message proceed if all good
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }
}
