package org.example.sem_backend.main_service.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@org.springframework.context.annotation.Configuration
@ComponentScan(basePackages = {
        "org.example.sem_backend.modules.equipment_module",
        "org.example.sem_backend.common_module",
        "org.example.sem_backend.modules.room_module"
})
@EnableAsync
@EnableScheduling
public class MainServiceConfig implements WebMvcConfigurer {
//    @Value("${hadoop.hdfs.url}")
//    private String hdfsUrl;
//    @Value("${hadoop.home.dir}")
//    private String hadoopHomeDir;

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

    @Bean(name = "taskExecutorPool")
    public ThreadPoolTaskExecutor taskExecutorPool() {
        int availableCores = Runtime.getRuntime().availableProcessors();
        int ioRatio = 4; // Tỷ lệ I/O

        // Công thức: Số thread = số core * (1 + ioRatio)
        int threadPoolSize = availableCores * (1 + ioRatio);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(availableCores); // Core pool = số core
        executor.setMaxPoolSize(threadPoolSize);
        executor.setQueueCapacity(100); // Giảm queue để tránh tích tụ task
        executor.setThreadNamePrefix("TaskExecutorPool-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "primaryTaskScheduler")
    public ThreadPoolTaskScheduler taskSchedulerPool() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors()); // Pool size = số core
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

    @Bean
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorParameter(true)
                .parameterName("mediaType")
                .ignoreAcceptHeader(false)
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);

        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}
