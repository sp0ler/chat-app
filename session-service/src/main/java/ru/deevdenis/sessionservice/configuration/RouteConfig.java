package ru.deevdenis.sessionservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.deevdenis.sessionservice.service.SessionHandlerService;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouteConfig {

    @Bean
    public RouterFunction<ServerResponse> composedRoutes(SessionHandlerService handlerService) {
        return route(POST("/session"), handlerService::saveSession)
                .andRoute(GET("/session"), handlerService::getSession);
    }
}
