package org.example.sem_backend.main_service.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@ComponentScan(basePackages = {
        "org.example.sem_backend.modules.equipment_module",
        "org.example.sem_backend.common_module",
        "org.example.sem_backend.modules.room_module"
})
public class MainServiceConfig implements WebMvcConfigurer {

    // Cấu hình ApplicationEventMulticaster sử dụng TaskExecutor
    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster(
            @Qualifier("taskExecutorPool") TaskExecutor taskExecutorPool) {

        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        multicaster.setTaskExecutor(taskExecutorPool);
        return multicaster;
    }

    // Cấu hình CORS cho toàn bộ ứng dụng
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Nguồn gốc được phép
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // Phương thức được phép
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Header được phép
        configuration.setAllowCredentials(true); // Cho phép cookie hoặc thông tin xác thực

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cấu hình cho tất cả endpoint
        return source;
    }

    // Tạo ThreadPoolTaskExecutor
    @Bean(name = "taskExecutorPool")
    public ThreadPoolTaskExecutor taskExecutorPool() {
        int totalCpuCores = Runtime.getRuntime().availableProcessors();
        int allocatedCores = (int) Math.ceil((totalCpuCores * 2.0) / 3); // Sử dụng 2/3 CPU cores
        int ioRatio = 4; // Tác vụ I/O chiếm 4 lần thời gian xử lý

        // Chia tài nguyên giữa TaskExecutor và TaskScheduler
        int executorCores = allocatedCores / 2; // Một nửa dành cho TaskExecutor
        int threadPoolSize = executorCores * (1 + ioRatio);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolSize / 2);
        executor.setMaxPoolSize(threadPoolSize);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("TaskExecutorPool-");
        executor.initialize();
        return executor;
    }

    // Tạo ThreadPoolTaskScheduler
    @Bean(name = "primaryTaskScheduler")
    public ThreadPoolTaskScheduler taskSchedulerPool() {
        int totalCpuCores = Runtime.getRuntime().availableProcessors();
        int allocatedCores = (int) Math.ceil((totalCpuCores * 2.0) / 3); // Sử dụng 2/3 CPU cores

        // Chia tài nguyên giữa TaskExecutor và TaskScheduler
        int schedulerCores = allocatedCores / 2; // Một nửa dành cho TaskScheduler

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(schedulerCores); // Tài nguyên dành riêng cho TaskScheduler
        scheduler.setThreadNamePrefix("TaskSchedulerPool-");
        scheduler.initialize();
        return scheduler;
    }

    // Định nghĩa TaskExecutor từ ThreadPoolTaskExecutor
    @Bean
    public TaskExecutor taskExecutor(@Qualifier("taskExecutorPool") ThreadPoolTaskExecutor taskExecutorPool) {
        return taskExecutorPool;
    }

    // Định nghĩa TaskScheduler từ ThreadPoolTaskScheduler
    @Bean
    public ThreadPoolTaskScheduler taskScheduler(@Qualifier("primaryTaskScheduler") ThreadPoolTaskScheduler taskSchedulerPool) {
        return taskSchedulerPool;
    }
}
