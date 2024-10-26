package org.example.sem_backend.modules.equipment_module.domain.mapper;

import org.example.sem_backend.modules.equipment_module.domain.dto.response.GetEquipmentResponseDto;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquipmentDetailMapper {
    @Mapping(source = "equipment.name", target = "equipmentName")
    @Mapping(source = "equipment.category", target = "equipmentCategory")
    @Mapping(source = "room.description", target = "roomDescription")
    @Mapping(source = "status", target = "currentStatus")
    GetEquipmentResponseDto toResponse(EquipmentDetail equipmentDetail);
}
