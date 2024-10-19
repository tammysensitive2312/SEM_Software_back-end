package org.example.sem_backend.main_service.container.services;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.equipment_module.service.EquipmentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ServiceContainer {

    private final EquipmentService equipmentService;

    @Bean
    public EquipmentService equipmentService() {
        return equipmentService;
    }
}
