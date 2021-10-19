package com.example.microservices_cloudgateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Component
public class CustomPreFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        log.info("Pre-filter execute");

        HttpHeaders headers = exchange.getRequest().getHeaders();
        Set<String> keys = headers.keySet();

        keys.forEach(k -> {
            String value = headers.getFirst(k);
            log.info("Header key: {}. Header value: {}", k, value);
        });

        return chain.filter(exchange);
    }
}
