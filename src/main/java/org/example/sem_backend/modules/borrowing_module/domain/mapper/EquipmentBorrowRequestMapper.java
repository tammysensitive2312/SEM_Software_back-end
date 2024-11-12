package org.example.sem_backend.modules.borrowing_module.domain.mapper;

import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestSummaryDTO;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EquipmentBorrowRequestDetailMapper.class)
public interface EquipmentBorrowRequestMapper {

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "expectedReturnDate", target = "expectedReturnDate")
    @Mapping(target = "status", constant = "NOT_BORROWED") // Set mặc định
    @Mapping(source = "equipmentItems", target = "borrowRequestDetails")
    EquipmentBorrowRequest toEntity(EquipmentBorrowRequestDTO dto);

    @Mapping(source = "borrowRequest.user.id", target = "userId")
    @Mapping(source = "borrowRequest.expectedReturnDate", target = "expectedReturnDate")
    @Mapping(source = "borrowRequest.borrowRequestDetails", target = "equipmentItems")
    EquipmentBorrowRequestDTO toDto(EquipmentBorrowRequest borrowRequest);

    @Mapping(source = "uniqueID", target = "uniqueID")
    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "comment", target = "comment")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "expectedReturnDate", target = "expectedReturnDate")
    @Mapping(source = "createAt", target = "createdAt")
    @Mapping(target = "equipmentItems", ignore = true)
    EquipmentBorrowRequestSummaryDTO toSummaryDto(EquipmentBorrowRequest entity);
}
