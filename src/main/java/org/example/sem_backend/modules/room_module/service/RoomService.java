package org.example.sem_backend.modules.room_module.service;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.exception.ResourceConflictException;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.room_module.domain.dto.request.RoomRequest;
import org.example.sem_backend.modules.room_module.domain.dto.response.RoomResponse;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.example.sem_backend.modules.room_module.domain.mapper.RoomMapper;
import org.example.sem_backend.modules.room_module.enums.RoomStatus;
import org.example.sem_backend.modules.room_module.enums.RoomType;
import org.example.sem_backend.modules.room_module.repository.RoomRepository;
import org.example.sem_backend.modules.room_module.repository.RoomSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService{

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Transactional(readOnly = true)
    public List<RoomResponse> findAvailableRooms(String type, LocalDate date, String period) {
        // Chuyển đổi thứ thành khung giờ bắt đầu và kết thúc
        LocalDateTime startTime = convertPeriodToStartTime(date, period);
        LocalDateTime endTime = convertPeriodToEndTime(date, period);

        // Lấy danh sách phòng trống từ repository
        List<Room> rooms = roomRepository.findAvailableRooms(type, startTime, endTime);

        if (rooms.isEmpty()) {
            throw new ResourceConflictException("Không có phòng nào khả dụng trong thời gian yêu cầu", "ROOM_MODULE");
        }
        // Map kết quả sang DTO
        return rooms.stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    public void addRoom(RoomRequest request) {
        if (roomRepository.existsByRoomName(request.getRoomName())) {
            throw new ResourceConflictException("Room name already exists", "ROOM-MODULE");
        }

        Room room = roomMapper.toEntity(request);
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
    }

    @Override
    public void updateRoom(RoomRequest request, Integer id) {
        Room room = roomRepository.findById(id.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found", "ROOM-MODULE"));
        try {
            roomMapper.partialUpdate(request, room);
            roomRepository.save(room);
        } catch (Exception e) {
            throw new RuntimeException("Error from ROOM-MODULE" + e);
        }
    }

    private LocalDateTime convertPeriodToStartTime(LocalDate date, String period) {
        // Giả sử thời gian của các tiết học được quy định
        return switch (period) {
            case "tiết 1" -> date.atTime(6, 55);
            case "tiết 2" -> date.atTime(7, 55);
            case "tiết 3" -> date.atTime(9, 0);
            case "tiết 4" -> date.atTime(9, 50);
            case "tiết 5" -> date.atTime(10, 40);
            default -> throw new IllegalArgumentException("tham số tiết học không hợp lệ");
        };
    }

    private LocalDateTime convertPeriodToEndTime(LocalDate date, String period) {
        return switch (period) {
            case "tiết 1" -> date.atTime(7, 50);
            case "tiết 2" -> date.atTime(8, 40);
            case "tiết 3" -> date.atTime(9, 45);
            case "tiết 4" -> date.atTime(10, 35);
            case "tiết 5" -> date.atTime(11, 30);
            default -> throw new IllegalArgumentException("tham số tiết học không hợp lệ");
        };
    }

    @Override
    public Page<RoomResponse> filterRoomsByTypeAndStatus(RoomType type, RoomStatus status, Pageable pageable) {
        String typeStr = type != null ? type.name() : null;
        String statusStr = status != null ? status.name() : null;

        Page<Room> roomPage = roomRepository.findByTypeAndStatus(typeStr, statusStr, pageable);
        if (roomPage == null) {
            throw new ResourceNotFoundException("không có phòng nào thỏa mãn yêu cầu", "ROOM-MODULE");
        }
        return roomPage.map(roomMapper::toResponse);
    }


    @Transactional(readOnly = true)
    public List<RoomResponse> findRooms(Integer capacity, String comparisonOperator, String roomCondition) {
        Specification<Room> spec = Specification.where(RoomSpecification.hasCapacity(capacity, comparisonOperator))
                .and(RoomSpecification.hasRoomCondition(roomCondition));

        List<RoomResponse> rooms = roomRepository.findAll(spec)
                .stream()
                .map(roomMapper::toResponse)
                .toList();

        if (rooms.isEmpty()) {
            throw new ResourceConflictException("Không có phòng nào đáp ứng yêu cầu", "ROOM_MODULE");
        }
        return rooms;
    }

    @Override
    public List<RoomResponse> searchRoom(String keyword) {
        if (keyword.isBlank()) {
            throw new ResourceNotFoundException("Từ khóa tìm kiếm không hợp lệ", "ROOM-MODULE");
        }
        List<Room> rooms = roomRepository.searchRoom(keyword);
        if (rooms.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy phòng nào", "ROOM-MODULE");
        }
        return rooms.stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }
}
