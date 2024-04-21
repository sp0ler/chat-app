package ru.deevdenis.sessionservice;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.deevdenis.sessionservice.configuration.AppConfig;
import ru.deevdenis.sessionservice.service.SessionHandlerService;

import java.util.UUID;

@WebFluxTest
@ContextConfiguration(classes = {AppConfig.class, SessionHandlerService.class})
public class AbstractTest {

    protected static final String PATH = "/session";
    protected static final String SENDER_LOGIN_ID = UUID.randomUUID().toString();
    protected static final String RECIPIENT_LOGIN_ID = UUID.randomUUID().toString();

    @Autowired
    protected ApplicationContext context;

    protected static WebTestClient webClient;

    @BeforeEach
    void init() {
        webClient = WebTestClient.bindToApplicationContext(context).build();
    }
}
