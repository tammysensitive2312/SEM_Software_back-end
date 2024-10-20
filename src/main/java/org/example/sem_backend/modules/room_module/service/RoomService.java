package org.example.sem_backend.modules.room_module.service;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.modules.room_module.domain.dto.AvailableRoomDto;
import org.example.sem_backend.modules.room_module.domain.entity.Room;
import org.example.sem_backend.modules.room_module.domain.mapper.RoomMapper;
import org.example.sem_backend.modules.room_module.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Transactional(readOnly = true)
    public List<AvailableRoomDto> findAvailableRooms(String type, LocalDate date, String period) {
        // Chuyển đổi period thành khung giờ bắt đầu và kết thúc
        String startTime = convertPeriodToStartTime(date, period);
        String endTime = convertPeriodToEndTime(date, period);

        // Lấy danh sách phòng trống từ repository
        List<Room> rooms = roomRepository.findAvailableRooms(type, startTime, endTime);

        // Map kết quả sang DTO
        return rooms.stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }

    private String convertPeriodToStartTime(LocalDate date, String period) {
        // Giả sử thời gian của các tiết học được quy định
        return switch (period) {
            case "tiết 1" -> date.atTime(6, 55).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            case "tiết 2" -> date.atTime(7, 55).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            case "tiết 3" -> date.atTime(9, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            case "tiết 4" -> date.atTime(9, 50).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            case "tiết 5" -> date.atTime(10, 40).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            default -> throw new IllegalArgumentException("tham số tiết học không hợp lệ");
        };
    }

    private String convertPeriodToEndTime(LocalDate date, String period) {
        return switch (period) {
            case "tiết 1" -> date.atTime(7, 50).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            case "tiết 2" -> date.atTime(8, 40).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            case "tiết 3" -> date.atTime(9, 45).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            case "tiết 4" -> date.atTime(10, 35).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            case "tiết 5" -> date.atTime(11, 30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            default -> throw new IllegalArgumentException("tham số tiết học không hợp lệ");
        };
    }
}
