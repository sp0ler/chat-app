package ru.deevdenis.sessionservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.deevdenis.sessionservice.service.SessionHandlerService;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouteConfig {

    private static final String PATH = "/session";

    @Bean
    public RouterFunction<ServerResponse> composedRoutes(SessionHandlerService handlerService) {
        return route(POST(PATH), handlerService::saveSession)
                .andRoute(GET(PATH), handlerService::getSession)
                .andRoute(DELETE(PATH), handlerService::deleteSession);
    }
}
