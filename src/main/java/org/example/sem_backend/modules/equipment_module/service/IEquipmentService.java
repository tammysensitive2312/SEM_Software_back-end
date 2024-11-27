package org.example.sem_backend.modules.equipment_module.service;

import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentResponse;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.UpdateEquipmentRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.CreateEquipmentRequest;
import org.example.sem_backend.modules.equipment_module.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IEquipmentService {
    CompletableFuture<Void> addEquipment(CreateEquipmentRequest request);

    void updateEquipment(Long id, UpdateEquipmentRequest request);

    Page<EquipmentResponse> filterEquipment(Category category, Pageable pageable);

    List<EquipmentResponse> searchEquipments(String keyword);
}
