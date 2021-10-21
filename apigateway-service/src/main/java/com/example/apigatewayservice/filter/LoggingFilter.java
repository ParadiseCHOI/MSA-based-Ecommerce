package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() { super(Config.class); }
    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

    @Override
    public GatewayFilter apply(Config config) {
/*
        // GatewayFilter filter = new OrderedGatewayFilter(((exchange, chain) -> {
        //     ServerHttpRequest req = exchange.getRequest();
        //     ServerHttpResponse res = exchange.getResponse();
        //
        //     log.info("Logging Filter baseMessage: {}", config.getBaseMessage());
        //     if (config.isPreLogger()) {
        //         log.info("Logging PRE Filter: request uri -> {}", req.getURI());
        //     }
        //
        //     return chain.filter(exchange).then(Mono.fromRunnable(() -> {
        //         if (config.isPostLogger()) {
        //             log.info("Logging POST Filter: response code -> {}", res.getStatusCode());
        //         }
        //     }));
        // }), Ordered.LOWEST_PRECEDENCE);
        //
        // return filter;
*/

        // Custom Pre Filter
        // Suppose we can extract JWT and perform Authentication
        return (exchange, chain) -> {
            // spring 5 부터 비동기 처리에 사용됨
            // Represents a reactive server-side HTTP request
            ServerHttpRequest req = exchange.getRequest();
            // Represents a reactive server-side HTTP response
            ServerHttpResponse res = exchange.getResponse();

            log.info("Logging Filter baseMessage: {}", config.getBaseMessage());
            if (config.isPreLogger()) {
                log.info("Logging PRE Filter: request uri -> {}", req.getURI());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Logging POST Filter: response code -> {}", res.getStatusCode());
                }
            }));
        };
    }
}
