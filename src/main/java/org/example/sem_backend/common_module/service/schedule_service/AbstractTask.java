package org.example.sem_backend.common_module.service.schedule_service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
public abstract class AbstractTask implements ScheduledTask, Runnable {
    private final LocalDateTime executionTime;
    private final Logger logger;

    @Override
    public void execute() {
        // Do nothing
    }

    /**
     *
     */
    @Override
    public void run() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        Runtime runtime = Runtime.getRuntime();

        logger.info("Task Resource Usage at {} - " +
                        "Memory: Used={}MB, Free={}MB, Total={}MB, Max={}MB, " +
                        "CPU Load: {}, " +
                        "Thread: ID={}, Name={}, Priority={}",
                executionTime,
                (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024),
                runtime.freeMemory() / (1024 * 1024),
                runtime.totalMemory() / (1024 * 1024),
                runtime.maxMemory() / (1024 * 1024),
                osBean.getSystemLoadAverage(),
                Thread.currentThread().getId(),
                Thread.currentThread().getName(),
                Thread.currentThread().getPriority()
        );
        execute();
    }
}
