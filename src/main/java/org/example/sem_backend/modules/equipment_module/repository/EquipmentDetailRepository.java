package org.example.sem_backend.modules.equipment_module.repository;

import org.example.sem_backend.modules.equipment_module.domain.entity.EquipmentDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentDetailRepository extends JpaRepository<EquipmentDetail, Long> {
  Page<EquipmentDetail> findAllByOrderByRoomNumberAsc(Pageable pageable);
}