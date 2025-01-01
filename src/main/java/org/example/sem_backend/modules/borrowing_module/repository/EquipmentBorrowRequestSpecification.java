package org.example.sem_backend.modules.borrowing_module.repository;

import org.example.sem_backend.modules.borrowing_module.domain.entity.EquipmentBorrowRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

/**
 * Specification Class handle params to query {@link EquipmentBorrowRequest}
 * @Param userId
 * @Param statuses
 * @Param date
 * @Param username
 */
public class EquipmentBorrowRequestSpecification {

    public static Specification<EquipmentBorrowRequest> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<EquipmentBorrowRequest> hasStatuses(List<EquipmentBorrowRequest.Status> statuses) {
        return (root, query, criteriaBuilder) ->
                root.get("status").in(statuses);
    }

    public static Specification<EquipmentBorrowRequest> expectedReturnDateBefore(LocalDate date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("expectedReturnDate"), date);
    }

    public static Specification<EquipmentBorrowRequest> expectedReturnDateAfter(LocalDate date) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("expectedReturnDate"), date);
    }

    public static Specification<EquipmentBorrowRequest> userUsernameContains(String username) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("username")), "%" + username.toLowerCase() + "%");
    }
    
}
