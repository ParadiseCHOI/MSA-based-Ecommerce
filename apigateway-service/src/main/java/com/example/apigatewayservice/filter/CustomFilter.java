package com.example.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter() { super(Config.class); }
    public static class Config { }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        // Suppose we can extract JWT and perform Authentication
        return (exchange, chain) -> {
            // spring 5 부터 비동기 처리에 사용됨
            // Represents a reactive server-side HTTP request
            ServerHttpRequest req = exchange.getRequest();
            // Represents a reactive server-side HTTP response
            ServerHttpResponse res = exchange.getResponse();

            log.info("Custom PRE filter: request id -> {}", req.getId());

            // Custom Post Filter
            // Suppose we can call error response handler based on error code.
            // Mono: webflux 의 단일 반환값
            return chain.filter(exchange).then(Mono.fromRunnable(()-> {
                log.info("Custom POST filter: response code -> {}", res.getStatusCode());
            }));
        };
    }
}
