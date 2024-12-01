package org.example.sem_backend.modules.equipment_module.service;

import org.example.sem_backend.modules.equipment_module.domain.dto.request.EquipmentDetailRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.request.UpdateEquipmentDetailLocationRequest;
import org.example.sem_backend.modules.equipment_module.domain.dto.response.EquipmentDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IEquipmentDetailService {
    void addEquipmentDetail(EquipmentDetailRequest request);

    void updateEquipmentDetailLocation(List<Long> equipmentDetailId, Long roomId);

    void updateEquipmentDetail(Long id, EquipmentDetailRequest request);

    Page<EquipmentDetailResponse> getEquipmentDetailsByEquipmentId(Long equipmentId, int page, int size);

    Page<EquipmentDetailResponse> getEquipmentDetailsByRoomId(Integer roomId, Pageable pageable);

    List<EquipmentDetailResponse> searchEquipmentDetail(String keyword);

    void deleteEquipmentDetail(Long id);
}
