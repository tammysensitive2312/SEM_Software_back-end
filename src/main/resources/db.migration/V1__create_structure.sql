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
CREATE TABLE IF NOT EXISTS sem_db.rooms (
unique_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
capacity    INT NOT NULL,
status        ENUM('AVAILABLE',
    'BROKEN',
    'IN_USE'),
type         ENUM('CLASSROOM',
    'LABORATORY',
    'MEETING_ROOM',
    'WAREHOUSE',
    'OFFICE'),
room_name   VARCHAR(255) NULL
);

-- 3. Tạo bảng equipment (không có phụ thuộc)
CREATE TABLE IF NOT EXISTS sem_db.equipments (
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
CREATE TABLE IF NOT EXISTS sem_db.equipment_details (
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
