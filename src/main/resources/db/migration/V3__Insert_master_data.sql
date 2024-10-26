/*
insert into sem_db.equipment (id, create_at, updated_at, broken_quantity, category, total_quantity, usable_quantity)
values  (1, '2024-10-19 15:02:08.000000', '2024-10-19 15:02:08.000000', 1, 'TEACHING_EQUIPMENT', 10, 9),
        (2, '2024-10-19 15:02:08.000000', '2024-10-19 15:02:08.000000', 0, 'LABORATORY_EQUIPMENT', 5, 5),
        (3, '2024-10-19 15:02:08.000000', '2024-10-19 15:02:08.000000', 2, 'INFORMATION_TECHNOLOGY_EQUIPMENT', 8, 6),
        (4, '2024-10-20 00:29:27.000000', '2024-10-20 00:29:27.000000', 1, 'TEACHING_EQUIPMENT', 10, 9),
        (5, '2024-10-20 00:29:27.000000', '2024-10-20 00:29:27.000000', 0, 'LABORATORY_EQUIPMENT', 5, 5),
        (6, '2024-10-20 00:29:27.000000', '2024-10-20 00:29:27.000000', 2, 'INFORMATION_TECHNOLOGY_EQUIPMENT', 8, 6),
        (7, '2024-10-20 16:31:24.000000', '2024-10-20 16:31:24.000000', 1, 'TEACHING_EQUIPMENT', 10, 9),
        (8, '2024-10-20 16:31:24.000000', '2024-10-20 16:31:24.000000', 0, 'LABORATORY_EQUIPMENT', 5, 5),
        (9, '2024-10-20 16:31:24.000000', '2024-10-20 16:31:24.000000', 2, 'INFORMATION_TECHNOLOGY_EQUIPMENT', 8, 6);

insert into sem_db.equipment_detail (id, create_at, updated_at, code, description, operating_hours, purchase_date, equipment_id, room_id, status)
values  (1, '2024-10-20 16:31:27.000000', '2024-10-20 16:31:27.000000', 'PRJ001', 'Projector for teaching', 1200, '2023-01-01', 1, 1, 'USABLE'),
        (2, '2024-10-20 16:31:27.000000', '2024-10-20 16:31:27.000000', 'LPT001', 'Dell Laptop', 800, '2023-03-15', 3, 1, 'USABLE'),
        (3, '2024-10-20 16:31:27.000000', '2024-10-20 16:31:27.000000', 'MIC001', 'Microscope', 500, '2023-05-20', 2, 2, 'BROKEN'),
        (4, '2024-10-20 16:31:27.000000', '2024-10-20 16:31:27.000000', 'PRT001', 'HP Laser Printer', 1500, '2022-12-10', 3, 2, 'OCCUPIED'),
        (5, '2024-10-20 16:31:27.000000', '2024-10-20 16:31:27.000000', 'PRJ002', 'Sony Projector', 300, '2023-08-08', 1, 3, 'USABLE'),
        (6, '2024-10-20 16:31:27.000000', '2024-10-20 16:31:27.000000', 'RTR001', 'Cisco Router', 1000, '2023-09-01', 3, 4, 'USABLE');

insert into sem_db.room (unique_id, capacity, status, type, description)
values  (1, 30, 'AVAILABLE', 'Classroom', '101'),
        (2, 20, 'AVAILABLE', 'Laboratory', '102'),
        (3, 50, 'IN_USE', 'Conference', '201'),
        (4, 15, 'BROKEN', 'Classroom', '202'),
        (5, 25, 'AVAILABLE', 'Laboratory', '203');

insert into sem_db.room_schedules (unique_id, end_time, start_time, user, room_id)
values  (1, '2024-10-21 10:00:00.000000', '2024-10-21 08:00:00.000000', 'user1', 1),
        (2, '2024-10-21 12:00:00.000000', '2024-10-21 09:00:00.000000', 'user2', 2),
        (3, '2024-10-21 14:00:00.000000', '2024-10-21 13:00:00.000000', 'user3', 1),
        (4, '2024-10-21 11:00:00.000000', '2024-10-21 10:00:00.000000', 'user4', 3),
        (5, '2024-10-21 16:00:00.000000', '2024-10-21 14:00:00.000000', 'user5', 4);

insert into sem_db.users (id, username, password, created_at, updated_at, role)
values  (1, 'admin', 'truong', '2024-10-17 16:02:44', '2024-10-17 16:02:44', null),
        (2, 'truong', 'test', '2024-10-24 13:57:17', '2024-10-24 13:57:17', null),
        (3, 'testUser', 'testPassword', '2024-10-24 13:57:17', '2024-10-24 13:57:17', null),
        (4, 'sbm', 'hihi', '2024-10-24 14:28:59', '2024-10-24 15:01:23', null);
 */