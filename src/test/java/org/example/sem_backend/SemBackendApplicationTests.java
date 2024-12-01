package org.example.sem_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@SpringBootTest(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration")
class SemBackendApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void generateDocumentation() {
        // Phân tích các mô-đun trong ứng dụng
        var modules = ApplicationModules.of(SemBackendApplication.class).verify();

        // Sử dụng Documenter để tạo tài liệu PlantUML cho các mô-đun
        new Documenter(modules)
                .writeModulesAsPlantUml()              // Tạo sơ đồ tổng quan các mô-đun
                .writeIndividualModulesAsPlantUml();   // Tạo sơ đồ chi tiết cho từng mô-đun
    }

    @Test
    void verifyModules() {
        ApplicationModules.of(SemBackendApplication.class).verify();
    }
}
