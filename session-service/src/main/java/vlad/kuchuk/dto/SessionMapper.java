package vlad.kuchuk.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import vlad.kuchuk.entity.Session;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SessionMapper {

    @Mapping(target = "openingTime", expression = "java(java.time.LocalDateTime.now())")
    Session toEntity(SessionRequest sessionRequest);

    SessionResponse toDto(Session session);
}