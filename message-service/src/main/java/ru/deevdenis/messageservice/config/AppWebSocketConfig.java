package ru.deevdenis.messageservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapi.java.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import reactor.core.publisher.Sinks;
import ru.deevdenis.messageservice.service.MessageHandler;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class AppWebSocketConfig {

    @Bean
    public HandlerMapping webSocketHandlerMapping(MessageHandler messageHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws", messageHandler::handle);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    @Bean
    public Sinks.Many<Object> sink() {
        return Sinks.many().replay().all();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
