package ru.deevdenis.sessionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.deevdenis.sessionservice.exception.DataNotFoundException;
import ru.deevdenis.sessionservice.model.SessionBody;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionHandlerService {

    private static final ConcurrentMap<String, String> sessions = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    public Mono<ServerResponse> saveSession(ServerRequest request) {
        return request
                .bodyToMono(SessionBody.class)
                .doOnNext(entity -> log.info("[SAVE] Received entity: {}", entity))
                .doOnNext(body -> sessions.put(body.getHost(), body.getId()))
                .flatMap(body -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(body), body.getClass()))
                .doOnError(ex -> log.error(ex.getMessage(), ex))
                .onErrorResume(Exception.class, this::handleException);
    }

    public Mono<ServerResponse> getSession(ServerRequest request) {
        return request
                .bodyToMono(SessionBody.class)
                .doOnNext(entity -> log.info("[SEARCH] Received entity: {}", entity))
                .map(SessionBody::getHost)
                .map(sessions::get)
                .doOnNext(id -> log.info("[SEARCH] Founded id: {}", id))
                .flatMap(id -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(id), id.getClass()))
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
