package ru.deevdenis.sessionservice.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Named;

import java.util.List;

public interface CommonMapper<R, T> {

    @Named("toDto")
    R toDto(T source);

    @Named("fromDto")
    T fromDto(R source);

    @IterableMapping(qualifiedByName = "toDto")
    List<R> toDtoList(List<T> source);

    @IterableMapping(qualifiedByName = "fromDto")
    List<T> fromDtoList(List<R> source);

}
