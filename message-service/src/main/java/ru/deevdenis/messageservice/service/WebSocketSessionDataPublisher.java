package ru.deevdenis.messageservice.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import ru.deevdenis.messageservice.model.WSMessage;

import java.util.function.Consumer;

@Slf4j
@Getter
@RequiredArgsConstructor
public class WebSocketSessionDataPublisher implements Consumer<Object> {

    private final String loginId;
    private final WebSocketSession session;

    @Override
    public void accept(Object o) {
        if (o instanceof WSMessage wsMessage) {
            log.info("Message to be sent for id %s".formatted(wsMessage.recipientLoginId()));

            if (loginId.equalsIgnoreCase(wsMessage.senderLoginId())) {
                session.send(Mono.just(session.textMessage(wsMessage.message()))).subscribe();
            }
        }
    }
}
