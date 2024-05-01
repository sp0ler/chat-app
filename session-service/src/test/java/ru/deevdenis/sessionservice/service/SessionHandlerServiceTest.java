package ru.deevdenis.sessionservice.service;

import org.junit.jupiter.api.Test;
import org.openapi.java.model.SessionBody;
import org.springframework.http.MediaType;
import ru.deevdenis.sessionservice.AbstractTest;

class SessionHandlerServiceTest extends AbstractTest {

    @Test
    void postSessionSuccessTest() {
        SessionBody sessionBody = new SessionBody().id(SENDER_LOGIN_ID).text("text");
        webClient
                .post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(sessionBody)
                .exchange()
                .expectStatus()
                .isOk();
    }
}
