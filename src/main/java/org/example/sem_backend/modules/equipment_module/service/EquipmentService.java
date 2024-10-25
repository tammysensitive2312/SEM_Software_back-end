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

    /**
 * Retrieves a paginated list of equipment details sorted by room number in ascending order.
 *
 * @param pageable The pagination and sorting parameters.
 * @return A paginated list of {@link GetEquipmentResponseDto} objects representing the equipment details.
 * @throws ResourceNotFoundException If no equipment details are found.
 */
@Override
public Page<GetEquipmentResponseDto> getAllEquipmentSortedByRoom(Pageable pageable) {
    Page<EquipmentDetail> equipmentDetails = equipmentDetailRepository.findAllByOrderByRoomAsc(pageable);
    if (equipmentDetails.isEmpty()) {
        throw new ResourceNotFoundException("Không tìm thấy thiết bị nào.", "EQUIPMENT_MODULE");
    }
    return equipmentDetails.map(equipmentMapper::toDto);
}
}
