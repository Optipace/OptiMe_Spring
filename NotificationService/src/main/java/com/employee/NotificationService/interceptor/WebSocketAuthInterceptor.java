//package com.employee.NotificationService.interceptor;
//
//import com.employee.NotificationService.util.JwtUtil;
//import lombok.AllArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.messaging.support.MessageHeaderAccessor;
//import org.springframework.stereotype.Component;
//
//import java.security.Principal;
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class WebSocketAuthInterceptor implements ChannelInterceptor {
//
//    private final JwtUtil jwtUtil;
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if(accessor != null && accessor.getCommand() != null){
//            System.out.println("STOMP command received: "+accessor.getCommand());
//        }
//        // Initial CONNECT frame
//        if(accessor != null && (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.STOMP.equals(accessor.getCommand()))){
//            // Extract the Authorization header from the STOMP payload
//            List<String> authorization = accessor.getNativeHeader("Authorization");
//
//            if(authorization != null && !authorization.isEmpty()){
//                String bearerToken = authorization.get(0);
//                if(bearerToken.startsWith("Bearer ")){
//                    String token = bearerToken.substring(7);
//
//                    try{
//                        jwtUtil.validateToken(token);
//
//                        // Extract the employeeId and officially register it to this WebSocket session
//                        String employeeId = jwtUtil.extractEmployeeId(token);
//
//                        accessor.setUser(new Principal() {
//                            @Override
//                            public String getName() {
//                                return employeeId;
//                            }
//                        });
//                        log.info("WebSocket Authenticated for: "+employeeId);
//                        return message;
//                    }catch (Exception e){
//                        log.error("WebSocket Connection rejected: Invalid JWT token");
////                        throw new IllegalArgumentException("Invalid token");
//                        return null;
//                    }
//                }else{
//                    log.error("WebSocket Connection rejected: Missing Authorization header");
////                    throw new IllegalArgumentException("No Token provided");
//                    return null;
//                }
//            }
//            return message; // Let message proceed if all good
//        }
//        return ChannelInterceptor.super.preSend(message, channel);
//    }
//}
