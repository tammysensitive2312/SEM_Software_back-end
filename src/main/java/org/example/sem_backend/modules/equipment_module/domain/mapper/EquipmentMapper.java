package org.example.sem_backend.modules.equipment_module.domain.mapper;

import org.example.sem_backend.modules.equipment_module.domain.dto.EquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.domain.dto.GetEquipmentResponseDto;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {

    Equipment toEquipment(EquipmentRequest request);

    @Mapping(target = "category", source = "category.value")
    EquipmentResponse toEquipmentResponse(Equipment equipment);

    EquipmentDetail toEquipmentDetail(EquipmentRequest equipmentRequest);
}
