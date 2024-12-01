package org.example.sem_backend;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class LoggingTestClass {
    @Test
    public void testLogging() {
        // Create an appender to capture logs
        Layout<?> layout = PatternLayout.newBuilder().withPattern("%p: %m%n").build();
        Appender appender = ConsoleAppender.newBuilder()
                .setName("Console")
                .setLayout(layout)
                .build();
        appender.start();

        // Add the appender to the logger (this can be configured in the Log4j2 config as well)
        org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger) org.apache.logging.log4j.LogManager.getLogger(LoggingTestClass.class);
        logger.addAppender(appender);

        // Test logs
        log.trace("Trace level log");
        log.debug("Debug level log");
        log.info("Info level log");
        log.warn("Warning level log");
        log.error("Error level log");

        // Verify if any of the logs appear in the console output
        String logs = appender.getLayout().toSerializable(null).toString();
        assertTrue(logs.contains("Trace level log"));
        assertTrue(logs.contains("Debug level log"));
        assertTrue(logs.contains("Info level log"));
        assertTrue(logs.contains("Warning level log"));
        assertTrue(logs.contains("Error level log"));

        // Clean up
        appender.stop();
    }
}