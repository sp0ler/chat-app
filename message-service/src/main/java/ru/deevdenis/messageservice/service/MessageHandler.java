package ru.deevdenis.messageservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapi.java.api.DefaultApi;
import org.openapi.java.model.SessionBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import ru.deevdenis.messageservice.model.WSMessage;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageHandler implements WebSocketHandler {

    private static final ConcurrentSkipListSet<String> recipientSet = new ConcurrentSkipListSet<>();

    private static final String MESSAGE_ID = "messageId";
    private static final String SENDER_LOGIN_ID = "senderLoginId";
    private static final String RECIPIENT_LOGIN_ID = "recipientLoginId";

    private final Sinks.Many<Object> sink;
    private final DefaultApi sessionServiceApi;

    @Value("${session.timeout}")
    private int sessionTimeout;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(session.getHandshakeInfo().getUri());
        Map<String,String> queryParams = builder.build().getQueryParams().toSingleValueMap();
        String messageId = queryParams.get(MESSAGE_ID);
        String senderLoginId = queryParams.get(SENDER_LOGIN_ID);
        String recipientLoginId = queryParams.get(RECIPIENT_LOGIN_ID);

        recipientSet.add(recipientLoginId);

        log.info("Websocket connected: %s".formatted(senderLoginId));

        SessionBody sessionBody = new SessionBody()
                .senderLoginId(senderLoginId)
                .recipientLoginId(recipientLoginId);
        sessionServiceApi.saveSession(sessionBody).subscribe();


        Disposable subscribe = sink.asFlux()
                .subscribe(new WebSocketSessionDataPublisher(recipientLoginId, session));

        Flux<WebSocketMessage> messageFlux = session
                .receive()
                .timeout(Duration.ofMinutes(sessionTimeout))
                .share();

        Flux<String> input = messageFlux
                .filter(webSocketMessage -> webSocketMessage.getType() == WebSocketMessage.Type.TEXT)
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(text -> {
                    log.info("Список recipientLoginId: {}", recipientSet);

                    if (!recipientSet.contains(senderLoginId)) {
                        SessionBody recipientBodySender = new SessionBody()
                                .senderLoginId(senderLoginId)
                                .recipientLoginId(recipientLoginId)
                                .text(text);
                        sessionServiceApi.getSession(recipientBodySender).subscribe();
                    } else {
                        log.info("Received message from %s".formatted(senderLoginId));
                        sink.tryEmitNext(
                                new WSMessage(messageId, senderLoginId, recipientLoginId, text)
                        );
                    }


                })
                .doFinally((ignored -> {
                    recipientSet.remove(recipientLoginId);

                    SessionBody senderBodySender = new SessionBody()
                            .senderLoginId(senderLoginId);
                    sessionServiceApi.deleteSession(senderBodySender).subscribe();
                    session.close();
                    subscribe.dispose();
                    log.info("Websocket terminated: %s".formatted(senderLoginId));
                }));

        return Flux.merge(input).then();
    }

}
