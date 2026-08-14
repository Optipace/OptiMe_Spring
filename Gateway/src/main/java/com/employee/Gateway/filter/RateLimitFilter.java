package com.employee.Gateway.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(-1)
public class RateLimitFilter implements GlobalFilter {
    private final Map<String, Bucket> cache=new ConcurrentHashMap<>();
    private Bucket createBucket(){
        Bandwidth limit=Bandwidth.builder().capacity(5).refillGreedy(5, Duration.ofMinutes(1)).build();
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, GatewayFilterChain gatewayFilterChain){
        String path=serverWebExchange.getRequest().getURI().getPath();
        if(path.startsWith("/ws")){
            return gatewayFilterChain.filter(serverWebExchange);
        }
        if(!path.startsWith("/api/")){
            return gatewayFilterChain.filter(serverWebExchange);
        }
        String ip=serverWebExchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        Bucket bucket=cache.computeIfAbsent(ip,k->createBucket());
        if(bucket.tryConsume(1)){
            return gatewayFilterChain.filter(serverWebExchange);
        }
        serverWebExchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        return serverWebExchange.getResponse().setComplete();
    }
}
