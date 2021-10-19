package com.example.microservices_cloudgateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Configuration
public class GlobalFiltersConfiguration {

    @Bean
    public GlobalFilter globalPrePostFilter() {

        return (exchange, chain) -> {

            log.info("GlobalPrePostFilter running up");

            String request = exchange.getRequest().toString();

            log.info("Request: {}", request);

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {

                String uri = exchange.getRequest().getURI().toString();

                log.info("The request URI: {}", uri);
            }));
        };
    }

}
