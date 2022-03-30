package com.ddkolesnik.ddkapi.configuration;

import org.mapstruct.Builder;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * @author Alexandr Stegnin
 */
@MapperConfig(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    builder = @Builder(disableBuilder = true)
)
public interface MapStructConfig {
}

