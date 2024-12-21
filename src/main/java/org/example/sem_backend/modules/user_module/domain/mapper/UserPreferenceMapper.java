package org.example.sem_backend.modules.user_module.domain.mapper;

import org.example.sem_backend.modules.user_module.domain.dto.UserPreferenceDto;
import org.example.sem_backend.modules.user_module.domain.entity.UserPreference;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserPreferenceMapper {
    UserPreference toEntity(UserPreferenceDto userPreferenceDto);

    UserPreferenceDto toDto(UserPreference userPreference);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserPreference partialUpdate(UserPreferenceDto userPreferenceDto, @MappingTarget UserPreference userPreference);
}