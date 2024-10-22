package org.example.sem_backend.modules.room_module.domain.mapper;

import org.example.sem_backend.modules.room_module.domain.dto.request.RoomRequest;
import org.example.sem_backend.modules.room_module.domain.dto.response.RoomResponse;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    Room toEntity(RoomRequest roomRequest);

    @Mapping(target = "status", source = "status.value")
    @Mapping(target = "type", source = "type.value")
    RoomResponse toResponse(Room room);
}
