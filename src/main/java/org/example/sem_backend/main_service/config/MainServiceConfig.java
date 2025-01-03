package org.example.sem_backend.main_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.LinkedHashSet;
import java.util.List;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@Configuration
@ComponentScan(basePackages = {
        "org.example.sem_backend.modules.equipment_module",
        "org.example.sem_backend.common_module",
        "org.example.sem_backend.modules.room_module"
})
public class MainServiceConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

//    @Bean
//    public TemplateEngine templateEngine(List<ITemplateResolver> templateResolvers) {
//        SpringTemplateEngine engine = new SpringTemplateEngine();
//        engine.setTemplateResolvers(new LinkedHashSet<>(templateResolvers));
//        return engine;
//    }
}
