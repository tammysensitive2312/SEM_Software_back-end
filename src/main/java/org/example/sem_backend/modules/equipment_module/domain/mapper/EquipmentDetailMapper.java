package org.example.sem_backend.modules.equipment_module.domain.mapper;

import org.example.sem_backend.modules.equipment_module.domain.dto.EquipmentDetailRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.EquipmentDetailResponse;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquipmentDetailMapper {
    @Mapping(source = "equipment.equipmentName", target = "equipmentName")
    @Mapping(source = "equipment.category", target = "category")
    @Mapping(source = "room.roomName", target = "roomName")
    EquipmentDetailResponse toResponse(EquipmentDetail equipmentDetail);
}
