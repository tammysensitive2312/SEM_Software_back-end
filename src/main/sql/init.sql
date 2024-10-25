-- Tạo bảng room với cột condition đã sửa thành room_condition để tránh lỗi từ khóa SQL
create table room
(
    unique_id      bigint auto_increment
        primary key,
    available_from varchar(255) null,
    available_to   varchar(255) null,
    capacity       int          not null,
    number         varchar(255) null,
    room_condition varchar(255) null,
    type           varchar(255) null
);

-- Tạo bảng room_schedules với dữ liệu mẫu
create table room_schedules
(
    unique_id  bigint auto_increment
        primary key,
    end_time   varchar(255) null,
    start_time varchar(255) null,
    user       varchar(255) null,
    room_id    bigint       not null,
    constraint FKooaitydu342v3x24fso7r5hfd
        foreign key (room_id) references room (unique_id)
);

-- Dữ liệu mẫu cho bảng room
INSERT INTO room (capacity, number, type, room_condition)
VALUES
    (30, '101', 'phòng học', 'AVAILABLE'),
    (20, '102', 'phòng thí nghiệm', 'AVAILABLE'),
    (50, '201', 'phòng hội thảo', 'IN_USE'),
    (15, '202', 'phòng học', 'UNDER_MAINTENANCE'),
    (25, '203', 'phòng thí nghiệm', 'BROKEN');

-- Dữ liệu mẫu cho bảng room_schedules
INSERT INTO room_schedules (end_time, start_time, user, room_id)
VALUES
    ('2024-10-21 10:00', '2024-10-21 08:00', 'user1', 1),
    ('2024-10-21 12:00', '2024-10-21 09:00', 'user2', 2),
    ('2024-10-21 14:00', '2024-10-21 13:00', 'user3', 1),
    ('2024-10-21 11:00', '2024-10-21 10:00', 'user4', 3),
    ('2024-10-21 16:00', '2024-10-21 14:00', 'user5', 4);

-- Dữ liệu mẫu cho bảng equipment
INSERT INTO equipment (total_quantity, usable_quantity, broken_quantity, category, create_at, updated_at) VALUES
                                                                                                              (10, 9, 1, 'TEACHING_EQUIPMENT', NOW(), NOW()),
                                                                                                              (5, 5, 0, 'LABORATORY_EQUIPMENT', NOW(), NOW()),
                                                                                                              (8, 6, 2, 'INFORMATION_TECHNOLOGY_EQUIPMENT', NOW(), NOW());

-- Dữ liệu mẫu cho bảng equipment_detail
INSERT INTO equipment_detail (purchase_date, description, code, current_status, operating_hours, equipment_id, room_id, create_at, updated_at) VALUES
                                                                                                                                                   ('2023-01-01', 'Projector for teaching', 'PRJ001', 'Active', 1200, 1, 1, NOW(), NOW()),
                                                                                                                                                   ('2023-03-15', 'Dell Laptop', 'LPT001', 'Active', 800, 3, 1, NOW(), NOW()),
                                                                                                                                                   ('2023-05-20', 'Microscope', 'MIC001', 'Active', 500, 2, 2, NOW(), NOW()),
                                                                                                                                                   ('2022-12-10', 'HP Laser Printer', 'PRT001', 'Broken', 1500, 3, 2, NOW(), NOW()),
                                                                                                                                                   ('2023-08-08', 'Sony Projector', 'PRJ002', 'Active', 300, 1, 3, NOW(), NOW()),
                                                                                                                                                   ('2023-09-01', 'Cisco Router', 'RTR001', 'Active', 1000, 3, 4, NOW(), NOW());

ALTER TABLE room
DROP COLUMN available_from,
    DROP COLUMN available_to