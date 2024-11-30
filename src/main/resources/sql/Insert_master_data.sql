-- Nhóm thiết bị CNTT (Information Technology)
INSERT INTO equipments
(code, equipment_name, category, in_use_quantity, total_quantity, usable_quantity, broken_quantity, create_at, updated_at, version)
VALUES
    ('LPT', 'Laptop', 'INFORMATION_TECHNOLOGY_EQUIPMENT', 8, 10, 9, 1, NOW(), NOW(), 1),
    ('MNT', 'Màn Hình', 'INFORMATION_TECHNOLOGY_EQUIPMENT', 6, 8, 7, 1, NOW(), NOW(), 1),
    ('PRJ', 'Máy Chiếu', 'INFORMATION_TECHNOLOGY_EQUIPMENT', 5, 6, 5, 1, NOW(), NOW(), 1);

-- Nhóm thiết bị Văn Phòng (Office Equipment)
INSERT INTO equipments
(code, equipment_name, category, in_use_quantity, total_quantity, usable_quantity, broken_quantity, create_at, updated_at, version)
VALUES
    ('PRN', 'Máy In', 'OFFICE_EQUIPMENT', 3, 4, 3, 1, NOW(), NOW(), 1),
    ('CAB', 'Tủ Hồ Sơ', 'OFFICE_EQUIPMENT', 5, 6, 5, 1, NOW(), NOW(), 1),
    ('DSK', 'Bàn Làm Việc', 'OFFICE_EQUIPMENT', 6, 7, 6, 1, NOW(), NOW(), 1);

-- Nhóm thiết bị Giảng Dạy (Teaching Equipment)
INSERT INTO equipments
(code, equipment_name, category, in_use_quantity, total_quantity, usable_quantity, broken_quantity, create_at, updated_at, version)
VALUES
    ('WBD', 'Bảng Trắng', 'TEACHING_EQUIPMENT', 4, 5, 4, 1, NOW(), NOW(), 1),
    ('MRO', 'Micro', 'TEACHING_EQUIPMENT', 3, 4, 3, 1, NOW(), NOW(), 1);

-- Nhóm thiết bị Điện (Electric Equipment)
INSERT INTO equipments
(code, equipment_name, category, in_use_quantity, total_quantity, usable_quantity, broken_quantity, create_at, updated_at, version)
VALUES
    ('AIR', 'Máy Lạnh', 'ELECTRIC_EQUIPMENT', 5, 6, 5, 1, NOW(), NOW(), 1),
    ('FAN', 'Quạt Điện', 'ELECTRIC_EQUIPMENT', 7, 8, 7, 1, NOW(), NOW(), 1);

-- Nhóm thiết bị Phòng Thí Nghiệm (Laboratory Equipment)
INSERT INTO equipments
(code, equipment_name, category, in_use_quantity, total_quantity, usable_quantity, broken_quantity, create_at, updated_at, version)
VALUES
    ('MIC', 'Kính Hiển Vi', 'LABORATORY_EQUIPMENT', 3, 4, 3, 1, NOW(), NOW(), 1),
    ('BSC', 'Cân Kỹ Thuật', 'LABORATORY_EQUIPMENT', 2, 3, 2, 1, NOW(), NOW(), 1);

-- Nhóm thiết bị Thể Thao (Sports Equipment)
INSERT INTO equipments
(code, equipment_name, category, in_use_quantity, total_quantity, usable_quantity, broken_quantity, create_at, updated_at, version)
VALUES
    ('BBL', 'Bóng Rổ', 'SPORTS_EQUIPMENT', 4, 5, 4, 1, NOW(), NOW(), 1),
    ('FTB', 'Bóng Đá', 'SPORTS_EQUIPMENT', 3, 4, 3, 1, NOW(), NOW(), 1);

-- Phòng học (Classroom)
INSERT INTO rooms
(room_name, type, capacity, status)
VALUES
    ('A101', 'CLASSROOM', 30, 'AVAILABLE'),
    ('A102', 'CLASSROOM', 35, 'AVAILABLE'),
    ('A103', 'CLASSROOM', 40, 'IN_USE');

-- Phòng thí nghiệm (Laboratory)
INSERT INTO rooms
(room_name, type, capacity, status)
VALUES
    ('LAB01', 'LABORATORY', 20, 'AVAILABLE'),
    ('LAB02', 'LABORATORY', 25, 'AVAILABLE'),
    ('LAB03', 'LABORATORY', 15, 'IN_USE');

-- Phòng họp (Meeting Room)
INSERT INTO rooms
(room_name, type, capacity, status)
VALUES
    ('MR01', 'MEETING_ROOM', 10, 'AVAILABLE'),
    ('MR02', 'MEETING_ROOM', 15, 'IN_USE');

-- Văn phòng (Office)
INSERT INTO rooms
(room_name, type, capacity, status)
VALUES
    ('OFF01', 'OFFICE', 5, 'AVAILABLE'),
    ('OFF02', 'OFFICE', 8, 'IN_USE'),
    ('OFF03', 'OFFICE', 6, 'AVAILABLE');

-- Kho (Warehouse)
INSERT INTO rooms
(room_name, type, capacity, status)
VALUES
    ('WH01', 'WAREHOUSE', 50, 'AVAILABLE'),
    ('WH02', 'WAREHOUSE', 40, 'AVAILABLE');

-- Chi tiết cho các thiết bị Laptop (CNTT)
INSERT INTO equipment_details
(equipment_id, serial_number, description, status, room_id, purchase_date, create_at, updated_at)
VALUES
    ((SELECT id FROM equipments WHERE code = 'LPT' AND equipment_name = 'Laptop'), 'LPT1', 'Laptop Dell', 'USABLE', 1, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'LPT' AND equipment_name = 'Laptop'), 'LPT2', 'Laptop Lenovo', 'USABLE', NULL, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'LPT' AND equipment_name = 'Laptop'), 'LPT3', 'Laptop HP', 'OCCUPIED', 1, '2024-11-30', NOW(), NOW());

-- Chi tiết cho các thiết bị Màn Hình (CNTT)
INSERT INTO equipment_details
(equipment_id, serial_number, description, status, room_id, purchase_date, create_at, updated_at)
VALUES
    ((SELECT id FROM equipments WHERE code = 'MNT' AND equipment_name = 'Màn Hình'), 'MNT1', 'Màn Hình Dell 24 inch', 'USABLE', 1, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'MNT' AND equipment_name = 'Màn Hình'), 'MNT2', 'Màn Hình LG', 'USABLE', NULL, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'MNT' AND equipment_name = 'Màn Hình'), 'MNT3', 'Màn Hình Samsung', 'OCCUPIED', 1, '2024-11-30', NOW(), NOW());

-- Chi tiết cho Máy Chiếu (CNTT)
INSERT INTO equipment_details
(equipment_id, serial_number, description, status, room_id, purchase_date, create_at, updated_at)
VALUES
    ((SELECT id FROM equipments WHERE code = 'PRJ' AND equipment_name = 'Máy Chiếu'), 'PRJ1', 'Máy Chiếu Epson', 'USABLE', 1, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'PRJ' AND equipment_name = 'Máy Chiếu'), 'PRJ2', 'Máy Chiếu BenQ', 'USABLE', NULL, '2024-11-30', NOW(), NOW());

-- Chi tiết cho Máy In (Văn Phòng)
INSERT INTO equipment_details
(equipment_id, serial_number, description, status, room_id, purchase_date, create_at, updated_at)
VALUES
    ((SELECT id FROM equipments WHERE code = 'PRN' AND equipment_name = 'Máy In'), 'PRN1', 'Máy In Canon', 'USABLE', 1, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'PRN' AND equipment_name = 'Máy In'), 'PRN2', 'Máy In HP', 'OCCUPIED', NULL, '2024-11-30', NOW(), NOW());

-- Chi tiết cho Tủ Hồ Sơ (Văn Phòng)
INSERT INTO equipment_details
(equipment_id, serial_number, description, status, room_id, purchase_date, create_at, updated_at)
VALUES
    ((SELECT id FROM equipments WHERE code = 'CAB' AND equipment_name = 'Tủ Hồ Sơ'), 'CAB1', 'Tủ Hồ Sơ Gỗ', 'USABLE', 1, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'CAB' AND equipment_name = 'Tủ Hồ Sơ'), 'CAB2', 'Tủ Hồ Sơ Sắt', 'USABLE', NULL, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'CAB' AND equipment_name = 'Tủ Hồ Sơ'), 'CAB3', 'Tủ Hồ Sơ Nhôm', 'OCCUPIED', 1, '2024-11-30', NOW(), NOW());

-- Chi tiết cho Bàn Làm Việc (Văn Phòng)
INSERT INTO equipment_details
(equipment_id, serial_number, description, status, room_id, purchase_date, create_at, updated_at)
VALUES
    ((SELECT id FROM equipments WHERE code = 'DSK' AND equipment_name = 'Bàn Làm Việc'), 'DSK1', 'Bàn Làm Việc Gỗ', 'USABLE', 1, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'DSK' AND equipment_name = 'Bàn Làm Việc'), 'DSK2', 'Bàn Làm Việc Inox', 'USABLE', NULL, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'DSK' AND equipment_name = 'Bàn Làm Việc'), 'DSK3', 'Bàn Làm Việc Composite', 'OCCUPIED', 1, '2024-11-30', NOW(), NOW());

-- Chi tiết cho Bảng Trắng (Giảng Dạy)
INSERT INTO equipment_details
(equipment_id, serial_number, description, status, room_id, purchase_date, create_at, updated_at)
VALUES
    ((SELECT id FROM equipments WHERE code = 'WBD' AND equipment_name = 'Bảng Trắng'), 'WBD1', 'Bảng Trắng Từ', 'USABLE', 1, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'WBD' AND equipment_name = 'Bảng Trắng'), 'WBD2', 'Bảng Trắng Thường', 'USABLE', NULL, '2024-11-30', NOW(), NOW());

-- Chi tiết cho Micro (Giảng Dạy)
INSERT INTO equipment_details
(equipment_id, serial_number, description, status, room_id, purchase_date, create_at, updated_at)
VALUES
    ((SELECT id FROM equipments WHERE code = 'MRO' AND equipment_name = 'Micro'), 'MRO1', 'Micro Không Dây', 'USABLE', 1, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'MRO' AND equipment_name = 'Micro'), 'MRO2', 'Micro Có Dây', 'USABLE', NULL, '2024-11-30', NOW(), NOW());

-- Chi tiết cho Máy Lạnh (Điện)
INSERT INTO equipment_details
(equipment_id, serial_number, description, status, room_id, purchase_date, create_at, updated_at)
VALUES
    ((SELECT id FROM equipments WHERE code = 'AIR' AND equipment_name = 'Máy Lạnh'), 'AIR1', 'Máy Lạnh Inverter', 'USABLE', 1, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'AIR' AND equipment_name = 'Máy Lạnh'), 'AIR2', 'Máy Lạnh Thường', 'USABLE', NULL, '2024-11-30', NOW(), NOW());

-- Chi tiết cho Quạt Điện (Điện)
INSERT INTO equipment_details
(equipment_id, serial_number, description, status, room_id, purchase_date, create_at, updated_at)
VALUES
    ((SELECT id FROM equipments WHERE code = 'FAN' AND equipment_name = 'Quạt Điện'), 'FAN1', 'Quạt Đứng', 'USABLE', 1, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'FAN' AND equipment_name = 'Quạt Điện'), 'FAN2', 'Quạt Treo Tường', 'USABLE', NULL, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'FAN' AND equipment_name = 'Quạt Điện'), 'FAN3', 'Quạt Để Bàn', 'OCCUPIED', 1, '2024-11-30', NOW(), NOW());

-- Chi tiết cho Kính Hiển Vi (Phòng Thí Nghiệm)
INSERT INTO equipment_details
(equipment_id, serial_number, description, status, room_id, purchase_date, create_at, updated_at)
VALUES
    ((SELECT id FROM equipments WHERE code = 'MIC' AND equipment_name = 'Kính Hiển Vi'), 'KHV1', 'Kính Hiển Vi Quang Học', 'USABLE', 1, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'MIC' AND equipment_name = 'Kính Hiển Vi'), 'KHV2', 'Kính Hiển Vi Điện Tử', 'USABLE', NULL, '2024-11-30', NOW(), NOW());

-- Chi tiết cho Cân Kỹ Thuật (Phòng Thí Nghiệm)
INSERT INTO equipment_details
(equipment_id, serial_number, description, status, room_id, purchase_date, create_at, updated_at)
VALUES
    ((SELECT id FROM equipments WHERE code = 'BSC' AND equipment_name = 'Cân Kỹ Thuật'), 'BSC1', 'Cân Điện Tử Chính Xác', 'USABLE', 1, '2024-11-30', NOW(), NOW());

-- Chi tiết cho Bóng Rổ (Thể Thao)
INSERT INTO equipment_details
(equipment_id, serial_number, description, status, room_id, purchase_date, create_at, updated_at)
VALUES
    ((SELECT id FROM equipments WHERE code = 'BBL' AND equipment_name = 'Bóng Rổ'), 'BBL1', 'Bóng Rổ Chuyên Nghiệp', 'USABLE', 1, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'BBL' AND equipment_name = 'Bóng Rổ'), 'BBL2', 'Bóng Rổ Tập Luyện', 'USABLE', NULL, '2024-11-30', NOW(), NOW());

-- Chi tiết cho Bóng Đá (Thể Thao)
INSERT INTO equipment_details
(equipment_id, serial_number, description, status, room_id, purchase_date, create_at, updated_at)
VALUES
    ((SELECT id FROM equipments WHERE code = 'FTB' AND equipment_name = 'Bóng Đá'), 'FTB1', 'Bóng Đá FIFA', 'USABLE', 1, '2024-11-30', NOW(), NOW()),
    ((SELECT id FROM equipments WHERE code = 'FTB' AND equipment_name = 'Bóng Đá'), 'FTB2', 'Bóng Đá Tập Luyện', 'USABLE', NULL, '2024-11-30', NOW(), NOW());

INSERT INTO users
(create_at, updated_at, password, role, username)
VALUES
    (NOW(), NOW(), 'truong', 'admin', 'admin'),
    (NOW(), NOW(), 'pwd', 'user', 'truong')
