package org.example.sem_backend.main_service.container.repositories;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.equipment_module.repository.EquipmentDetailRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RepositoryContainer {

    private final EquipmentDetailRepository equipmentDetailRepository;

    @Bean
    public EquipmentDetailRepository equipmentDetailRepository() {
        return equipmentDetailRepository;
    }
}
