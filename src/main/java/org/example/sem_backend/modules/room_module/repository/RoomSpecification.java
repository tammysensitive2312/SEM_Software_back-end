package org.example.sem_backend.modules.room_module.repository;

import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.springframework.data.jpa.domain.Specification;

public class RoomSpecification {
    public static Specification<Room> hasCapacity(Integer capacity, String comparisonOperator) {
        return (root, query, criteriaBuilder) -> {
            if (capacity == null || comparisonOperator == null) {
                return criteriaBuilder.conjunction();
            }
            return switch (comparisonOperator) {
                case ">" -> criteriaBuilder.greaterThan(root.get("capacity"), capacity);
                case ">=" -> criteriaBuilder.greaterThanOrEqualTo(root.get("capacity"), capacity);
                case "<" -> criteriaBuilder.lessThan(root.get("capacity"), capacity);
                case "<=" -> criteriaBuilder.lessThanOrEqualTo(root.get("capacity"), capacity);
                case "=" -> criteriaBuilder.equal(root.get("capacity"), capacity);
                default -> throw new IllegalArgumentException("Operator không hợp lệ: " + comparisonOperator);
            };
        };
    }

    public static Specification<Room> hasRoomCondition(String roomCondition) {
        return (root, query, criteriaBuilder) -> {
            if (roomCondition == null || roomCondition.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("condition"), roomCondition);
        };
    }
}
