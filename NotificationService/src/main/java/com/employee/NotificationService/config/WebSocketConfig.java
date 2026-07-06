package com.employee.NotificationService.config;

import com.employee.NotificationService.interceptor.WebSocketAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor authInterceptor;

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
        // This is the URL Frontend will use to dial the "phone call"
        // e.g., ws://localhost:8086/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // Allows Frontend to connect from any origin
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
//        WebSocketMessageBrokerConfigurer.super.configureClientInboundChannel(registration);
        registration.interceptors(authInterceptor);
    }
}
