package org.example.sem_backend.main_service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@Configuration
@ComponentScan(basePackages = {
        "org.example.sem_backend.modules.equipment_module",
        "org.example.sem_backend.common_module",
        "org.example.sem_backend.modules.room_module"
})
public class MainServiceConfig implements WebMvcConfigurer {

}
