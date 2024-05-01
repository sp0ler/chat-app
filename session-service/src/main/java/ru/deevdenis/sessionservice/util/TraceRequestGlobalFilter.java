package ru.deevdenis.sessionservice.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TraceRequestGlobalFilter implements CommonGlobalFilter{

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerWebExchange newExchange = exchange;
        String id = requestId.apply(exchange);
        String uuid = UUID.randomUUID().toString();

        if (StringUtils.isEmpty(id)) {
            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .header(X_REQUEST_ID, uuid)
                    .build();
            newExchange = exchange.mutate().request(request).build();
        }

        id = exchange.getResponse().getHeaders().getFirst(X_REQUEST_ID);
        if (StringUtils.isEmpty(id)) {
            exchange.getResponse().getHeaders().set(X_REQUEST_ID, uuid);
        }

        return chain.filter(newExchange);
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
