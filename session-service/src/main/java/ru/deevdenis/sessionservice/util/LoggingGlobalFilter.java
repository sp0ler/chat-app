package ru.deevdenis.sessionservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class LoggingGlobalFilter implements CommonGlobalFilter {

    private static final String LOG_REQUEST = "[REQUEST] requestId={}, url={}, headers={}, body={}";
    private static final String LOG_RESPONSE = "[RESPONSE] requestId={}, headers={}, status code={}";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String id = requestId.apply(exchange);
        log.info(LOG_REQUEST, id, exchange.getRequest().getURI(), exchange.getRequest().getHeaders(), null);

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
