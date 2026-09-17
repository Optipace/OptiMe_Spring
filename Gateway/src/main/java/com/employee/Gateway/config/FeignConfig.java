package com.employee.Gateway.config;

import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    public Decoder feignDecoder() {
        return new JacksonDecoder();
    }
}