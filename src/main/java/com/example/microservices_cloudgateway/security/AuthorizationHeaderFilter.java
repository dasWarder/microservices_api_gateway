package com.example.microservices_cloudgateway.security;

import io.jsonwebtoken.Jwts;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class AuthorizationHeaderFilter
    extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

  @Value("${token.secret}")
  private String secret;

  public AuthorizationHeaderFilter() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();

      if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
        return onError(exchange, HttpStatus.UNAUTHORIZED);
      }

      String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
      String jwt = authHeader.replace("Bearer", "");

      if(!isValidToken(jwt)) {
        return onError(exchange, HttpStatus.UNAUTHORIZED);
      }

      return chain.filter(exchange);
    };
  }

  public static class Config {}

  private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(httpStatus);

    return response.setComplete();
  }

  private boolean isValidToken(String jwt) {

    String subject = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody().getSubject();

    if(subject.isEmpty() || Objects.isNull(subject)) {
      return false;
    }

    return true;
  }
}
