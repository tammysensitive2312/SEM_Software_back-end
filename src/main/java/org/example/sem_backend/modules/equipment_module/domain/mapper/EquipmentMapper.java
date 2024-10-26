package org.example.sem_backend.modules.equipment_module.domain.mapper;

import org.example.sem_backend.modules.equipment_module.domain.dto.GetEquipmentResponseDto;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {

    @Mapping(source = "room.description", target = "roomDescription")
    @Mapping(source = "room.type", target = "roomType")
    @Mapping(source = "equipment.category.description", target = "equipmentCategory")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "status.value", target = "currentStatus")
    @Mapping(source = "operatingHours", target = "operatingHours")
    GetEquipmentResponseDto toDto(EquipmentDetail equipmentDetail);
}