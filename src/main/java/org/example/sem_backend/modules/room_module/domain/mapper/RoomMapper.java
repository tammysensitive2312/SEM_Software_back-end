package org.example.sem_backend.modules.room_module.domain.mapper;

import org.example.sem_backend.modules.room_module.domain.dto.AvailableRoomDto;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {
    Room toEntity(AvailableRoomDto availableRoomDto);

    AvailableRoomDto toDto(Room room);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Room partialUpdate(AvailableRoomDto availableRoomDto, @MappingTarget Room room);
}