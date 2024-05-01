package ru.deevdenis.messageservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapi.java.model.SessionBody;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import ru.deevdenis.messageservice.model.WSMessage;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendMessageService {

    private final Sinks.Many<Object> sink;

    public Mono<ServerResponse> send(ServerRequest request) {

        return request
                .bodyToMono(SessionBody.class)
                .doOnNext(entity -> log.info("[RECEIVED] SendMessageService get: {}", entity))
                .doOnNext(body -> {
                    String senderLoginId = body.getSenderLoginId();
                    String recipientLoginId = body.getRecipientLoginId();
                    String text = body.getText();

                    sink.tryEmitNext(new WSMessage(UUID.randomUUID().toString(), senderLoginId, recipientLoginId, text));
                })
                .flatMap(body -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.empty(), Void.class));
    }

}
