package com.example.microservices_cloudgateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Configuration
public class GlobalFiltersConfiguration {

    @Bean
    @Order(2)
    public GlobalFilter globalPrePostLoggingFilter() {

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

    @Bean
    @Order(1)
    public GlobalFilter globalHeaderPreFilter() {

        return ((exchange, chain) -> {
            log.info("Pre-filter execute");

            HttpHeaders headers = exchange.getRequest().getHeaders();
            Set<String> keys = headers.keySet();

            keys.forEach(k -> {
                String value = headers.getFirst(k);
                log.info("Header key: {}. Header value: {}", k, value);
            });

            return chain.filter(exchange);
        });
    }

    @Bean
    @Order(3)
    public GlobalFilter globalFilter() {

        return (exchange, chain) -> {

            String id = exchange.getRequest().getId();

            log.info("Request id: {}", id);

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {

                HttpStatus statusCode = exchange.getResponse().getStatusCode();
                Set<String> keys = exchange.getResponse().getHeaders().keySet();

                keys.forEach(k -> {
                    String value = exchange.getResponse().getHeaders().getFirst(k);

                    log.info("Response key: {} with a value: {}", k, value);
                });

                log.info("Response status code: {}", statusCode.value());
            }));
        };
    }

}
