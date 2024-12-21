package org.example.sem_backend.modules.notification_module.service;

import java.util.List;

/**
 * Functional Interface to provide a way to pass a
 * function as a parameter to other function
 */
@FunctionalInterface
public interface UserIdProvider {
    List<Long> getUserIds();
}
