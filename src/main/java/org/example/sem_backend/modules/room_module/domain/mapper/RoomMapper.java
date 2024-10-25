package org.example.sem_backend.modules.room_module.domain.mapper;

import org.example.sem_backend.modules.room_module.domain.dto.RoomDto;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {

    @Mapping(target = "type", expression = "java(room.getType() != null ? room.getType().getDescription() : null)")
    @Mapping(target = "roomCondition", expression = "java(room.getRoomCondition() != null ? room.getRoomCondition().name() : null)")
    RoomDto toDto(Room room);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Room partialUpdate(RoomDto roomDto, @MappingTarget Room room);
}