package org.example.sem_backend.modules.borrowing_module.domain.mapper;

import org.example.sem_backend.modules.borrowing_module.domain.dto.room.GetRoomRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.room.RoomBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.entity.RoomBorrowRequest;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomBorrowRequestMapper {

    @Mapping(target = "uniqueID", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "room", ignore = true)
    RoomBorrowRequest toEntity(RoomBorrowRequestDTO dto);

    @Mapping(source = "room.roomName", target = "roomName")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "uniqueID", target = "uniqueId")
    GetRoomRequestDTO toDto(RoomBorrowRequest entity);

    // CreateRoomBorrowRequestDTO toDto(RoomBorrowRequest roomBorrowRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RoomBorrowRequest partialUpdate(RoomBorrowRequestDTO createRoomBorrowRequestDTO, @MappingTarget RoomBorrowRequest roomBorrowRequest);
}