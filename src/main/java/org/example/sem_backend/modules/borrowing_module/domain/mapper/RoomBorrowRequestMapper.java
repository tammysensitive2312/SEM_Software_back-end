package org.example.sem_backend.modules.borrowing_module.domain.mapper;

import org.example.sem_backend.modules.borrowing_module.domain.dto.RoomBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.entity.RoomBorrowRequest;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomBorrowRequestMapper {
    RoomBorrowRequestMapper INSTANCE = Mappers.getMapper(RoomBorrowRequestMapper.class);

    @Mapping(target = "uniqueID", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "room", ignore = true)
    RoomBorrowRequest toEntity(RoomBorrowRequestDTO dto);

    // CreateRoomBorrowRequestDTO toDto(RoomBorrowRequest roomBorrowRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RoomBorrowRequest partialUpdate(RoomBorrowRequestDTO createRoomBorrowRequestDTO, @MappingTarget RoomBorrowRequest roomBorrowRequest);
}