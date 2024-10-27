package org.example.sem_backend.modules.user_module.domain.mapper;

import org.example.sem_backend.modules.user_module.domain.dto.UpdateUserRequest;
import org.example.sem_backend.modules.user_module.domain.dto.UserDto;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto toDto(User user);

    UpdateUserRequest toDto(User user, @MappingTarget UpdateUserRequest anotherUserDto);

    User toEntity(UserDto userDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(UpdateUserRequest userDto, @MappingTarget User user);
}
