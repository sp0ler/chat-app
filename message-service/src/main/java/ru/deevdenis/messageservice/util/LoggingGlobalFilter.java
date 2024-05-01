package ru.deevdenis.messageservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingGlobalFilter implements CommonGlobalFilter {

    private static final String LOG_REQUEST = "[REQUEST] requestId={}, url={}, headers={}";
    private static final String LOG_RESPONSE = "[RESPONSE] requestId={}, headers={}, status code={}";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String id = requestId.apply(exchange);
        log.info(LOG_REQUEST, id, exchange.getRequest().getURI(), exchange.getRequest().getHeaders());
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() ->
                        log.info(
                                LOG_RESPONSE,
                                id,
                                exchange.getResponse().getHeaders(),
                                exchange.getResponse().getStatusCode())
                ));
    }

    @Override
    public int getOrder() {
        return -1;
    }


}
