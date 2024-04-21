package ru.deevdenis.sessionservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.deevdenis.sessionservice.AbstractTest;
import ru.deevdenis.sessionservice.model.SessionBody;

class SessionHandlerServiceTest extends AbstractTest {

    @Test
    void postSessionSuccessTest() {
        SessionBody sessionBody = new SessionBody(SENDER_LOGIN_ID, "",  "text");
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
