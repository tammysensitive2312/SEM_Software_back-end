package org.example.sem_backend.modules.equipment_module.service;

import org.example.sem_backend.modules.equipment_module.domain.dto.request.EquipmentDetailRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IEquipmentDetailService {
    void addEquipmentDetail(EquipmentDetailRequest request);

    void updateEquipmentDetailLocation(List<Long> equipmentDetailId, Long roomId);

    void updateEquipmentDetail(Long id, EquipmentDetailRequest request);

    Page<EquipmentDetailResponse> getEquipmentDetailsByRoomId(Integer roomId, Pageable pageable);

    Page<EquipmentDetailResponse> searchEquipmentDetail(String keyword, int page, int size);

    Page<EquipmentDetailResponse> getEquipmentDetailByEquipmentId(Long equipmentId, String keyword, String status, int page, int size);

    void deleteEquipmentDetail(Long id);
}
