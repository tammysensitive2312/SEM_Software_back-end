package org.example.sem_backend.modules.room_module.repository;

import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.springframework.data.jpa.domain.Specification;

public class RoomSpecification {

    public static Specification<Room> hasCapacity(Integer capacity, String comparisonOperator) {
        if (capacity == null || comparisonOperator == null) {
            return null;
        }

        return switch (comparisonOperator) {
            case ">" -> (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("capacity"), capacity);
            case ">=" ->
                    (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("capacity"), capacity);
            case "<" -> (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("capacity"), capacity);
            case "<=" ->
                    (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("capacity"), capacity);
            case "=" -> (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("capacity"), capacity);
            default -> throw new IllegalArgumentException("Invalid comparison operator: " + comparisonOperator);
        };
    }

    public static Specification<Room> hasRoomCondition(String roomCondition) {
        if (roomCondition == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("roomCondition"), roomCondition);
    }
}
