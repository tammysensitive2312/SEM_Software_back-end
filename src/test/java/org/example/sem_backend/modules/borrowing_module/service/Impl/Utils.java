package org.example.sem_backend.modules.borrowing_module.service.Impl;

import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowItemDTO;
import org.example.sem_backend.modules.borrowing_module.domain.dto.equipment.EquipmentBorrowRequestDTO;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequest;
import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequestDetail;
import org.example.sem_backend.modules.equipment_module.domain.entity.Equipment;
import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.example.sem_backend.modules.user_module.domain.entity.User;

import java.util.List;

public class Utils {

    public static User createUser(Long userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    public static Equipment createEquipment(String equipmentName, int usableQuantity) {
        Equipment equipment = new Equipment();
        equipment.setEquipmentName(equipmentName);
        equipment.setUsableQuantity(usableQuantity);
        return equipment;
    }

    public static EquipmentBorrowRequestDTO createValidBorrowRequestDTO(Long userId) {
        EquipmentBorrowRequestDTO requestDto = new EquipmentBorrowRequestDTO();
        requestDto.setUserId(userId);
        requestDto.setEquipmentItems(List.of(
                createBorrowItemDTO("Laptop", 2, "Good condition")
        ));
        return requestDto;
    }

    public static EquipmentBorrowItemDTO createBorrowItemDTO(String equipmentName, int quantity, String condition) {
        EquipmentBorrowItemDTO itemDto = new EquipmentBorrowItemDTO();
        itemDto.setEquipmentName(equipmentName);
        itemDto.setQuantityBorrowed(quantity);
        itemDto.setConditionBeforeBorrow(condition);
        return itemDto;
    }

    public static EquipmentBorrowRequest createBorrowRequest(User user) {
        EquipmentBorrowRequest request = new EquipmentBorrowRequest();
        request.setUniqueID(1L);
        request.setStatus(EquipmentBorrowRequest.Status.NOT_BORROWED);
        request.setUser(user);
        return request;
    }

    public static EquipmentBorrowRequestDetail createBorrowRequestDetail(Equipment equipment, int quantity) {
        EquipmentBorrowRequestDetail detail = new EquipmentBorrowRequestDetail();
        detail.setEquipment(equipment);
        detail.setQuantityBorrowed(quantity);
        return detail;
    }

    public static EquipmentDetail createEquipmentDetail(Equipment equipment, Long id) {
        EquipmentDetail equipmentDetail = new EquipmentDetail();
        equipmentDetail.setId(id);
        equipmentDetail.setEquipment(equipment);
        return equipmentDetail;
    }
}