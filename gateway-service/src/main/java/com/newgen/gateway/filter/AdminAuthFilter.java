package com.newgen.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AdminAuthFilter implements GlobalFilter, Ordered {

    private static final String ADMIN_PATH_PREFIX = "/newgen/admin";
    private static final String ADMIN_KEY_HEADER = "X-Admin-Key";

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (!path.startsWith(ADMIN_PATH_PREFIX)) {
            return chain.filter(exchange);
        }

        String providedKey = exchange.getRequest().getHeaders().getFirst(ADMIN_KEY_HEADER);

        if (adminSecretKey.equals(providedKey)) {
            return chain.filter(exchange);
        }

        log.warn("Unauthorized admin access attempt to {}", path);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
