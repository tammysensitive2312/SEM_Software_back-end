package org.example.sem_backend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;


class DocumentationTests {

    @Test
    void generateDocumentation() {
        // Tạo ApplicationModules mà không cần khởi động toàn bộ ứng dụng
        var modules = ApplicationModules.of(SemBackendApplication.class).verify();

        // Sử dụng Documenter để tạo tài liệu PlantUML cho các mô-đun
        new Documenter(modules)
                .writeAggregatingDocument();
    }

//    @Test
//    void generateDetailedClassDiagrams() {
//        var modules = ApplicationModules.of(SemBackendApplication.class).verify();
//
//        var canvasOptions = Documenter.CanvasOptions.defaults()
//                .withApiDetails()
//                .withClassDetails()
//                .withInterfaceDetails()
//                .withStyle(Style.UML) // Sử dụng UML style
//                .withColorSelector(module -> Colors.GREEN) // Tùy chỉnh màu sắc
//                .withTargetFileName("detailed-class-diagram"); // Tên file output
//
//        new Documenter(modules)
//                .writeModulesAsPlantUml(canvasOptions)
//                .writeIndividualModulesAsPlantUml(canvasOptions);
//    }

    @Test
    void verifyModules() {
        ApplicationModules.of(SemBackendApplication.class).verify();
    }
}
