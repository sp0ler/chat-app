package ru.deevdenis.sessionservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.openapi.java.model.SessionBody;
import ru.deevdenis.sessionservice.entity.SessionBodyDTO;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SessionBodyMapper extends CommonMapper<SessionBodyDTO, SessionBody> {
}
