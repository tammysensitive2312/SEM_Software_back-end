package org.example.sem_backend.modules.borrowing_module.service;


import java.util.List;

public interface InterfaceRequestService<T, D> {
    void processRequest(D requestDto);
    boolean validateRequest(D requestDto);
    void updateRequest(D requestDto);
    void approveRequest(Long requestId);
    void deleteRequestsByIds(List<Long> requestIds);
}
