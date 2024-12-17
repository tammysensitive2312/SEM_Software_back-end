package org.example.sem_backend.main_service.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class EventConfig {
    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster(
            @Qualifier("applicationTaskExecutor") TaskExecutor applicationTaskExecutor) {

        SimpleApplicationEventMulticaster multicaster =
                new SimpleApplicationEventMulticaster();

        multicaster.setTaskExecutor(applicationTaskExecutor);
        return multicaster;
    }

//    @Bean(name = "applicationTaskExecutor")
//    public TaskExecutor taskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(10);
//        executor.setMaxPoolSize(100);
//        executor.setQueueCapacity(50);
//        executor.setThreadNamePrefix("BorrowTask-");
//        executor.initialize();
//        return executor;
//    }
}
