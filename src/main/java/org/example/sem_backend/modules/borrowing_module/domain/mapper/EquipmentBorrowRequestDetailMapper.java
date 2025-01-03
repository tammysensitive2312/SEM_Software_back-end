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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EquipmentBorrowRequestDetailMapper {

    // Mapping từ DTO sang Entity
    @Mapping(source = "dto.equipmentDetailCodes", target = "equipmentDetails", qualifiedByName = "mapToEquipmentDetails")
    @Mapping(target = "borrowRequest", ignore = true)
    @Mapping(source = "equipmentName", target = "equipment.equipmentName")
    EquipmentBorrowRequestDetail toEntity(EquipmentBorrowItemDTO dto);

//    @Named("mapToEquipment")
//    default Equipment mapToEquipment(String equipmentName, EquipmentRepository equipmentRepository) {
//        return equipmentRepository.findByEquipmentName(equipmentName)
//                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found: " + equipmentName, "BORROWING_MODULE"));
//    }

    // Mapping từ Entity sang DTO
    @Mapping(source = "equipment.equipmentName", target = "equipmentName")
    @Mapping(source = "equipmentDetails", target = "equipmentDetailCodes", qualifiedByName = "mapEquipmentDetailSerialNumber")
    EquipmentBorrowItemDTO toDto(EquipmentBorrowRequestDetail detail);

    // Custom mapping để chuyển từ List<String> sang List<EquipmentDetail>
    @Named("mapToEquipmentDetails")
    default List<EquipmentDetail> mapToEquipmentDetails(List<String> serialNumbers) {
        if (serialNumbers == null) {
            return new ArrayList<>(); // if null, return empty list
        }
        // This should be implemented based on context, or provide `availableDetails` from service logic
        return serialNumbers.stream().map(serialNumber -> {

            EquipmentDetail detail = new EquipmentDetail();
            detail.setSerialNumber(serialNumber);
            return detail;
        }).collect(Collectors.toList());
    }

    // Mapping từ EquipmentBorrowRequest sang EquipmentBorrowRequestDetailsDTO
    @Mapping(source = "uniqueID", target = "requestId")
    @Mapping(source = "borrowRequestDetails", target = "details")
    EquipmentBorrowRequestDetailsDTO toDetailsDto(EquipmentBorrowRequest request);

    // Mapping từ EquipmentBorrowRequestDetail sang EquipmentBorrowRequestDetailDTO
    @Mapping(source = "uniqueId", target = "id")
    @Mapping(source = "equipment.equipmentName", target = "equipmentName")
    @Mapping(source = "equipmentDetails", target = "borrowedEquipmentDetailCodes", qualifiedByName = "mapEquipmentDetailSerialNumber")
    EquipmentBorrowRequestDetailDTO toDetailDto(EquipmentBorrowRequestDetail detail);

    @Named("mapEquipmentDetailSerialNumber")
    default List<String> mapEquipmentDetailSerialNumber(List<EquipmentDetail> equipmentDetails) {
        if (equipmentDetails == null) {
            return List.of();
        }
        return equipmentDetails.stream()
                .map(EquipmentDetail::getSerialNumber)
                .collect(Collectors.toList());
    }
}
