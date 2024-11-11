package org.example.sem_backend.modules.borrowing_module.service;


public interface InterfaceRequestService<T, D> {
    void processRequest(D requestDto);
    boolean validateRequest(D requestDto);
}
