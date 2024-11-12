package org.example.sem_backend.modules.borrowing_module.domain.mapper;

import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowItemDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestDetailDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestDetailsDTO;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequest;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequestDetail;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EquipmentBorrowRequestDetailMapper {

    // Mapping từ DTO sang Entity
    @Mapping(source = "dto.equipmentName", target = "equipment.name")
    @Mapping(source = "dto.equipmentDetailCodes", target = "equipmentDetails", qualifiedByName = "mapToEquipmentDetails")
    EquipmentBorrowRequestDetail toEntity(EquipmentBorrowItemDTO dto);

    // Mapping từ Entity sang DTO
    @Mapping(source = "equipment.name", target = "equipmentName")
    @Mapping(source = "equipmentDetails", target = "equipmentDetailCodes", qualifiedByName = "mapToDetailCodes")
    EquipmentBorrowItemDTO toDto(EquipmentBorrowRequestDetail detail);

    // Custom mapping để chuyển từ List<String> sang List<EquipmentDetail>
    @Named("mapToEquipmentDetails")
    default List<EquipmentDetail> mapToEquipmentDetails(List<String> detailCodes) {
        // This should be implemented based on context, or provide `availableDetails` from service logic
        return detailCodes.stream().map(code -> {
            EquipmentDetail detail = new EquipmentDetail();
            detail.setCode(code);
            return detail;
        }).collect(Collectors.toList());
    }

    // Custom mapping để chuyển từ List<EquipmentDetail> sang List<String>
    @Named("mapToDetailCodes")
    default List<String> mapToDetailCodes(List<EquipmentDetail> equipmentDetails) {
        if (equipmentDetails == null) {
            return null;
        }
        return equipmentDetails.stream()
                .map(EquipmentDetail::getCode)
                .collect(Collectors.toList());
    }

    // Mapping từ EquipmentBorrowRequest sang EquipmentBorrowRequestDetailsDTO
    @Mapping(source = "uniqueID", target = "requestId")
    @Mapping(source = "borrowRequestDetails", target = "details")
    EquipmentBorrowRequestDetailsDTO toDetailsDto(EquipmentBorrowRequest request);

    // Mapping từ EquipmentBorrowRequestDetail sang EquipmentBorrowRequestDetailDTO
    @Mapping(source = "uniqueId", target = "id")
    @Mapping(source = "equipment.name", target = "equipmentName")
    EquipmentBorrowRequestDetailDTO toDetailDto(EquipmentBorrowRequestDetail detail);
}
