package org.example.sem_backend.modules.equipment_module.service;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.equipment_module.domain.dto.GetEquipmentResponseDto;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.example.sem_backend.modules.equipment_module.domain.mapper.EquipmentMapper;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentDetailRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EquipmentService implements IEquipmentService {

    private final EquipmentDetailRepository equipmentDetailRepository;
    private final EquipmentMapper equipmentMapper;

    @Override
    public Page<GetEquipmentResponseDto> getAllEquipmentSortedByRoom(Pageable pageable) {
        Page<EquipmentDetail> equipmentDetails = equipmentDetailRepository.findAllByOrderByRoomNumberAsc(pageable);

        if (equipmentDetails.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy thiết bị nào.");
        }

        return equipmentDetails.map(equipmentMapper::toDto);
    }
}
