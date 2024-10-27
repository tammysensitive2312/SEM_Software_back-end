package org.example.sem_backend.modules.equipment_module.domain.mapper;

import org.example.sem_backend.modules.equipment_module.domain.dto.request.CreateEquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
    Equipment toEquipment(CreateEquipmentRequest request);

    @Mapping(target = "category", source = "category.description")
    EquipmentResponse toEquipmentResponse(Equipment equipment);

    EquipmentDetail toEquipmentDetail(CreateEquipmentRequest equipmentRequest);
}
