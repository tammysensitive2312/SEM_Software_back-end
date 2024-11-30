package org.example.sem_backend.modules.equipment_module.domain.mapper;

import org.example.sem_backend.modules.equipment_module.domain.dto.request.EquipmentDetailRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentDetailResponse;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquipmentDetailMapper {
    @Mapping(source = "equipment.equipmentName", target = "equipmentName")
    @Mapping(source = "equipment.category", target = "category")
    @Mapping(source = "room.roomName", target = "roomName")
    @Mapping(source = "status.value", target = "status")
    EquipmentDetailResponse toResponse(EquipmentDetail equipmentDetail);

    EquipmentDetail toEntity(EquipmentDetailRequest request);
}
