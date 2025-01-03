package org.example.sem_backend.modules.equipment_module.domain.mapper;

import org.example.sem_backend.modules.equipment_module.domain.dto.request.EquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {

    Equipment toEquipment(EquipmentRequest request);

    @Mapping(target = "category", source = "category.description")
    EquipmentResponse toEquipmentResponse(Equipment equipment);
}
