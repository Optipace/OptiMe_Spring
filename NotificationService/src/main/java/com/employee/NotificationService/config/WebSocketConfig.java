package com.employee.NotificationService.config;

import com.employee.NotificationService.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//    private final WebSocketAuthInterceptor authInterceptor;
    private final JwtUtil jwtUtil;
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
//        WebSocketMessageBrokerConfigurer.super.configureMessageBroker(config);
        // "/topic" is for broadcasting to EVERYONE (e.g., Company wide announcements)
        // "/queue" is for private, 1-on-1 messages (e.g., "Your leave is approved")
        config.enableSimpleBroker("/topic", "/queue");

        // Any message sent FROM Frontend TO the server must start with /app
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        WebSocketMessageBrokerConfigurer.super.registerStompEndpoints(registry);
        // The URL for Frontend will use to dial the "phone call"
        // e.g., ws://localhost:8080/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // Allows Frontend to connect from any origin
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        WebSocketMessageBrokerConfigurer.super.configureClientInboundChannel(registration);
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if(accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())){
                    log.info(">>>WEBSOCKET INTERCEPTOR TRIGGERED | Command: "+accessor.getCommand());

                    List<String> authorization = accessor.getNativeHeader("Authorization");

                    if(authorization == null || authorization.isEmpty()){
                        log.error("Websocket rejected: Missing Authorization header.");;
                        throw new MessageDeliveryException("Missing Token");
                    }

                    String bearerToken = authorization.get(0);
                    if(bearerToken.startsWith("Bearer ")){
                        String token = bearerToken.substring(7);

                        try{
                            jwtUtil.validateToken(token);
                            String employeeId = jwtUtil.extractEmployeeId(token);

                            accessor.setUser(new Principal() {
                                @Override
                                public String getName() {
                                    log.info("WebSocket Authenticated for: "+employeeId);
                                    return employeeId;
                                }
                            });
                        }catch (Exception e){
                            log.error("WebSocket rejected: Invalid Jwt Token.");
                            throw new MessageDeliveryException("Invalid token.");
                        }
                    }else {
                        log.error("WebSocket rejected : Token doesn't start with Bearer.");
                        throw new MessageDeliveryException("Invalid token format.");
                    }
                }
//                return ChannelInterceptor.super.preSend(message, channel);
                return message;
            }
        });
    }
}
