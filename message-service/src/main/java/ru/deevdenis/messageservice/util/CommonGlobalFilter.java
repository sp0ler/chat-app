package ru.deevdenis.messageservice.util;

import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;

import java.util.function.Function;

public interface CommonGlobalFilter extends WebFilter, Ordered {
    String X_REQUEST_ID = "x-request-id";

    Function<ServerWebExchange, String> requestId = exchange -> exchange.getRequest().getHeaders().getFirst(X_REQUEST_ID);
    Function<ServerWebExchange, String> responseId = exchange -> exchange.getResponse().getHeaders().getFirst(X_REQUEST_ID);

}
