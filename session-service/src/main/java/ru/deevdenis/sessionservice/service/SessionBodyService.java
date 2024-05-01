package ru.deevdenis.sessionservice.service;

import lombok.RequiredArgsConstructor;
import org.openapi.java.model.SessionBody;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.deevdenis.sessionservice.entity.SessionBodyDTO;
import ru.deevdenis.sessionservice.exception.DuplicateException;
import ru.deevdenis.sessionservice.mapper.SessionBodyMapper;
import ru.deevdenis.sessionservice.repository.SessionBodyRepository;

@Service
@RequiredArgsConstructor
public class SessionBodyService {

    private final SessionBodyMapper mapper;
    private final SessionBodyRepository repository;

    @Transactional
    public Mono<SessionBodyDTO> save(SessionBody body, String baseUrl) {
        SessionBodyDTO dto = mapper.toDto(body);
        dto.setBaseUrl(baseUrl);

        return repository.existsBySenderLoginId(body.getRecipientLoginId())
                .flatMap(isExist -> {
                    if (isExist) {
                        return Mono.error(new DuplicateException("Login %s already exist.".formatted(body.getRecipientLoginId())));
                    }

                    return Mono.empty();
                })
                .flatMap(skip -> repository.save(dto));
    }

    @Transactional
    public Mono<SessionBodyDTO> getByRecipientLoginId(String loginId) {
        return repository.findSessionBodyDTOByRecipientLoginId(loginId);
    }

    @Transactional
    public Mono<Void> deleteByRecipientLoginId(String loginId) {
        return repository.deleteByRecipientLoginId(loginId);
    }
}
