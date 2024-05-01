package ru.deevdenis.sessionservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapi.java.api.DefaultApi;
import org.openapi.java.model.SessionBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.deevdenis.sessionservice.exception.DataNotFoundException;
import ru.deevdenis.sessionservice.factory.MessageServiceApiFactory;
import ru.deevdenis.sessionservice.mapper.SessionBodyMapper;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionHandlerService {
    private static final ConcurrentMap<String, DefaultApi> WEB_CLIENTS = new ConcurrentHashMap<>();

    private final MessageServiceApiFactory messageServiceApi;
    private final SessionBodyService sessionBodyService;
    private final SessionBodyMapper mapper;

    public Mono<ServerResponse> saveSession(ServerRequest request) {
        String baseUrl = request.exchange().getRequest().getHeaders().getFirst("X-Upstream");

        return request
                .bodyToMono(SessionBody.class)
                .doOnNext(entity -> log.info("[SAVE] Received entity: {}", entity))
                .flatMap(body -> sessionBodyService.save(body, baseUrl))
                .flatMap(body -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(body), body.getClass()))
                .doOnError(ex -> log.error(ex.getMessage(), ex))
                .onErrorResume(Exception.class, this::handleException);
    }

    public Mono<ServerResponse> getSession(ServerRequest request) {
        return  request
                .bodyToMono(SessionBody.class)
                .doOnNext(entity -> log.info("[SEARCH] Received entity: {}", entity))
                .flatMap(entity -> sessionBodyService.getByRecipientLoginId(entity.getRecipientLoginId()))
                .flatMap(dto -> {
                    String baseUrl = dto.getBaseUrl();
                    if (Objects.isNull(baseUrl)) {
                        return Mono.error(new DataNotFoundException());
                    }

                    log.info("[SEARCH] Founded host: {}", baseUrl);
                    if (!WEB_CLIENTS.containsKey(baseUrl)) {
                        DefaultApi api = messageServiceApi.buildDefaultApi(baseUrl);
                        WEB_CLIENTS.put(baseUrl, api);
                    }

                    DefaultApi defaultApi = WEB_CLIENTS.get(baseUrl);
                    SessionBody sessionBody = mapper.fromDto(dto);
                    return defaultApi.sendMessage(sessionBody);
                })
                .flatMap(it -> Mono.defer(() -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.empty(), Void.class)))
                .doOnError(ex -> log.error(ex.getMessage(), ex))
                .onErrorResume(Exception.class, this::handleException).log();


    }

    public Mono<ServerResponse> deleteSession(ServerRequest request) {
        return request
                .bodyToMono(SessionBody.class)
                .doOnNext(entity -> log.info("[DELETE] Received entity: {}", entity))
                .flatMap(entity -> sessionBodyService.deleteByRecipientLoginId(entity.getSenderLoginId()))
                .flatMap(skip -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.empty(), Void.class))
                .doOnError(ex -> log.error(ex.getMessage(), ex))
                .onErrorResume(Exception.class, this::handleException);
    }


    private Mono<? extends ServerResponse> handleException(Exception ex) {
        if (ex instanceof NullPointerException) {
            return Mono.error(new DataNotFoundException("The session doesnt found."));
        }

        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }
}
