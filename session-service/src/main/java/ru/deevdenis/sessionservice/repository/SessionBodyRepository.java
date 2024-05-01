package ru.deevdenis.sessionservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.deevdenis.sessionservice.entity.SessionBodyDTO;

import java.util.UUID;

@Repository
public interface SessionBodyRepository extends ReactiveCrudRepository<SessionBodyDTO, UUID> {

    Mono<Boolean> existsBySenderLoginId(String senderLoginId);
    Mono<SessionBodyDTO> findSessionBodyDTOByRecipientLoginId(String recipientLoginId);

    Mono<Void> deleteByRecipientLoginId(String recipientLoginId);
}
