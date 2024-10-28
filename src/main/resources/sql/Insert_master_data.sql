-- Bắt đầu tạo bảng từ bảng cha đến bảng con

-- 1. Tạo bảng users (không có phụ thuộc)
CREATE TABLE IF NOT EXISTS sem_db.users (
                                            id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            username   VARCHAR(20) NOT NULL,
                                            password   VARCHAR(255) NOT NULL,
                                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,
                                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
                                            role       VARCHAR(255) NULL,
                                            CONSTRAINT UniqueUsername UNIQUE (username)
);

-- 2. Tạo bảng room (không có phụ thuộc)
CREATE TABLE IF NOT EXISTS sem_db.room (
                                           unique_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           capacity    INT NOT NULL,
                                           status      VARCHAR(255) NULL,
                                           type        VARCHAR(255) NULL,
                                           description VARCHAR(255) NULL
);

-- 3. Tạo bảng equipment (không có phụ thuộc)
CREATE TABLE IF NOT EXISTS sem_db.equipment (
                                                id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                create_at       DATETIME(6),
                                                updated_at      DATETIME(6),
                                                broken_quantity INT NOT NULL,
                                                category        ENUM('INFORMATION_TECHNOLOGY_EQUIPMENT',
                                                    'LABORATORY_EQUIPMENT',
                                                    'SPORTS_EQUIPMENT',
                                                    'TEACHING_EQUIPMENT'),
                                                total_quantity  INT NOT NULL,
                                                usable_quantity INT NOT NULL,
                                                name            VARCHAR(255) NOT NULL,
                                                CHECK (category IN ('INFORMATION_TECHNOLOGY_EQUIPMENT',
                                                                    'LABORATORY_EQUIPMENT',
                                                                    'SPORTS_EQUIPMENT',
                                                                    'TEACHING_EQUIPMENT'))
);

-- 4. Tạo bảng equipment_detail (phụ thuộc vào equipment và room)
CREATE TABLE IF NOT EXISTS sem_db.equipment_detail (
                                                       id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                       create_at       DATETIME(6) NULL,
                                                       updated_at      DATETIME(6) NULL,
                                                       code            VARCHAR(255) NULL,
                                                       description     VARCHAR(255) NULL,
                                                       operating_hours INT NOT NULL,
                                                       purchase_date   VARCHAR(255) NULL,
                                                       equipment_id    BIGINT NULL,
                                                       room_id         BIGINT NULL,
                                                       status          ENUM ('BROKEN', 'OCCUPIED', 'USABLE') NULL,
                                                       CONSTRAINT FKlinkToEquipment FOREIGN KEY (equipment_id) REFERENCES sem_db.equipments (id),
                                                       CONSTRAINT FKlinkToRoom FOREIGN KEY (room_id) REFERENCES sem_db.rooms (unique_id)
);

-- 5. Tạo bảng room_schedules (phụ thuộc vào room)
CREATE TABLE IF NOT EXISTS sem_db.room_schedules (
                                                     unique_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                     end_time   DATETIME(6) NULL,
                                                     start_time DATETIME(6) NULL,
                                                     user       VARCHAR(255) NULL,
                                                     room_id    BIGINT NOT NULL,
                                                     CONSTRAINT FKroomScheduleTime FOREIGN KEY (room_id) REFERENCES sem_db.rooms (unique_id)
);


-- 1. Đầu tiên insert users vì không phụ thuộc bảng nào
insert into sem_db.users (id, username, password, created_at, updated_at, role)
values  (1, 'admin', 'truong', '2024-10-17 16:02:44', '2024-10-17 16:02:44', null),
        (2, 'truong', 'test', '2024-10-24 13:57:17', '2024-10-24 13:57:17', null),
        (3, 'testUser', 'testPassword', '2024-10-24 13:57:17', '2024-10-24 13:57:17', null),
        (4, 'sbm', 'hihi', '2024-10-24 14:28:59', '2024-10-24 15:01:23', null);

-- 2. Insert room vì equipment_detail và room_schedules phụ thuộc vào room
insert into sem_db.rooms (room_name)
values  ('101'),
        ('102'),
        ('201'),
        ('202'),
        ('203');

-- 3. Insert equipment vì equipment_detail phụ thuộc vào equipment
insert into sem_db.equipments (id, create_at, updated_at, broken_quantity, category, total_quantity, usable_quantity, name)
values  (1, '2024-10-19 15:02:08.000000', '2024-10-19 15:02:08.000000', 1, 'TEACHING_EQUIPMENT', 10, 9, 'máy chiếu'),
        (2, '2024-10-19 15:02:08.000000', '2024-10-19 15:02:08.000000', 0, 'LABORATORY_EQUIPMENT', 5, 5, 'kính hiển vi'),
        (3, '2024-10-19 15:02:08.000000', '2024-10-19 15:02:08.000000', 2, 'INFORMATION_TECHNOLOGY_EQUIPMENT', 8, 6, 'laptop');


-- 4. Insert equipment_detail sau khi đã có room và equipment
insert into sem_db.equipment_details (id, create_at, updated_at, code, description, operating_hours, purchase_date, equipment_id, room_id, status)
values  (1, '2024-10-20 16:31:27.000000', '2024-10-20 16:31:27.000000', 'PRJ001', 'Projector for teaching', 1200, '2023-01-01', 1, 1, 'USABLE'),
        (2, '2024-10-20 16:31:27.000000', '2024-10-20 16:31:27.000000', 'LPT001', 'Dell Laptop', 800, '2023-03-15', 3, 1, 'USABLE'),
        (3, '2024-10-20 16:31:27.000000', '2024-10-20 16:31:27.000000', 'MIC001', 'Microscope', 500, '2023-05-20', 2, 2, 'BROKEN'),
        (4, '2024-10-20 16:31:27.000000', '2024-10-20 16:31:27.000000', 'PRT001', 'HP Laser Printer', 1500, '2022-12-10', 3, 2, 'OCCUPIED'),
        (5, '2024-10-20 16:31:27.000000', '2024-10-20 16:31:27.000000', 'PRJ002', 'Sony Projector', 300, '2023-08-08', 1, 3, 'USABLE'),
        (6, '2024-10-20 16:31:27.000000', '2024-10-20 16:31:27.000000', 'RTR001', 'Cisco Router', 1000, '2023-09-01', 3, 4, 'USABLE');

-- 5. Cuối cùng insert room_schedules sau khi đã có room
insert into sem_db.room_schedules (unique_id, end_time, start_time, user, room_id)
values  (1, '2024-10-21 10:00:00.000000', '2024-10-21 08:00:00.000000', 'user1', 1),
        (2, '2024-10-21 12:00:00.000000', '2024-10-21 09:00:00.000000', 'user2', 2),
        (3, '2024-10-21 14:00:00.000000', '2024-10-21 13:00:00.000000', 'user3', 1),
        (4, '2024-10-21 11:00:00.000000', '2024-10-21 10:00:00.000000', 'user4', 3),
        (5, '2024-10-21 16:00:00.000000', '2024-10-21 14:00:00.000000', 'user5', 4);