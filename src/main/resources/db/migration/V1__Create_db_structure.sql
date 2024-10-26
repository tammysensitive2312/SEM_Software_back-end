/*
DROP TABLE IF EXISTS sem_db.room_schedules;
DROP TABLE IF EXISTS sem_db.equipment_detail;
DROP TABLE IF EXISTS sem_db.equipment;
DROP TABLE IF EXISTS sem_db.room;
DROP TABLE IF EXISTS sem_db.users;

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
    CHECK (category IN ('INFORMATION_TECHNOLOGY_EQUIPMENT',
                        'LABORATORY_EQUIPMENT',
                        'SPORTS_EQUIPMENT',
                        'TEACHING_EQUIPMENT'))
);

create table if not exists sem_db.equipment_detail
(
    id              bigint auto_increment
        primary key,
    create_at       datetime(6)                           null,
    updated_at      datetime(6)                           null,
    code            varchar(255)                          null,
    description     varchar(255)                          null,
    operating_hours int                                   not null,
    purchase_date   varchar(255)                          null,
    equipment_id    bigint                                null,
    room_id         bigint                                null,
    status          enum ('BROKEN', 'OCCUPIED', 'USABLE') null,
    constraint FK276pncwmhcsy50rlr7ooea6qd
        foreign key (equipment_id) references sem_db.equipment (id),
    constraint FK521xfk2bv9goim1en8mnfuglv
        foreign key (room_id) references sem_db.room (unique_id)
);

create table if not exists sem_db.room
(
    unique_id   bigint auto_increment
        primary key,
    capacity    int          not null,
    status      varchar(255) null,
    type        varchar(255) null,
    description varchar(255) null
);

create table if not exists sem_db.room_schedules
(
    unique_id  bigint auto_increment
        primary key,
    end_time   datetime(6)  null,
    start_time datetime(6)  null,
    user       varchar(255) null,
    room_id    bigint       not null,
    constraint FKooaitydu342v3x24fso7r5hfd
        foreign key (room_id) references sem_db.room (unique_id)
);

create table if not exists sem_db.users
(
    id         bigint auto_increment
        primary key,
    username   varchar(20)                         not null,
    password   varchar(255)                        not null,
    created_at timestamp default CURRENT_TIMESTAMP null,
    updated_at timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    role       varchar(255)                        null,
    constraint username
        unique (username)
);
 */

