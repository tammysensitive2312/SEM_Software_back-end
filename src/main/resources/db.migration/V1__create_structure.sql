create table if not exists equipments
(
    id              bigint auto_increment
        primary key,
    create_at       datetime(6)                                                                                                                                           null,
    updated_at      datetime(6)                                                                                                                                           null,
    broken_quantity int                                                                                                                                                   not null,
    category        enum ('ELECTRIC_EQUIPMENT', 'INFORMATION_TECHNOLOGY_EQUIPMENT', 'LABORATORY_EQUIPMENT', 'SPORTS_EQUIPMENT', 'TEACHING_EQUIPMENT', 'OFFICE_EQUIPMENT') null,
    name            varchar(255)                                                                                                                                          null,
    total_quantity  int                                                                                                                                                   not null,
    usable_quantity int                                                                                                                                                   not null,
    version         bigint                                                                                                                                                null
);

create table if not exists flyway_schema_history
(
    installed_rank int                                 not null
        primary key,
    version        varchar(50)                         null,
    description    varchar(200)                        not null,
    type           varchar(20)                         not null,
    script         varchar(1000)                       not null,
    checksum       int                                 null,
    installed_by   varchar(100)                        not null,
    installed_on   timestamp default CURRENT_TIMESTAMP not null,
    execution_time int                                 not null,
    success        tinyint(1)                          not null
);

create index flyway_schema_history_s_idx
    on flyway_schema_history (success);

create table if not exists rooms
(
    unique_id bigint auto_increment
        primary key,
    capacity  int                                                                     not null,
    room_name varchar(255)                                                            not null,
    status    enum ('AVAILABLE', 'BROKEN', 'IN_USE')                                  null,
    type      enum ('CLASSROOM', 'LABORATORY', 'MEETING_ROOM', 'OFFICE', 'WAREHOUSE') null,
    constraint UK2tklvare2e5touoeqsdgdsdgm
        unique (room_name)
);

create table if not exists equipment_details
(
    id              bigint auto_increment
        primary key,
    create_at       datetime(6)                           null,
    updated_at      datetime(6)                           null,
    code            varchar(255)                          not null,
    description     varchar(255)                          null,
    operating_hours int                                   not null,
    purchase_date   varchar(255)                          not null,
    status          enum ('BROKEN', 'OCCUPIED', 'USABLE') null,
    equipment_id    bigint                                null,
    room_id         bigint                                null,
    constraint UKmymo71ouisl9bbkmptnwfea8k
        unique (code),
    constraint FK276pncwmhcsy50rlr7ooea6qd
        foreign key (equipment_id) references equipments (id),
    constraint FK521xfk2bv9goim1en8mnfuglv
        foreign key (room_id) references rooms (unique_id)
);

create table if not exists room_schedules
(
    unique_id  bigint auto_increment
        primary key,
    end_time   datetime(6)  null,
    start_time datetime(6)  null,
    user       varchar(255) null,
    room_id    bigint       null,
    constraint FKooaitydu342v3x24fso7r5hfd
        foreign key (room_id) references rooms (unique_id)
);

create table if not exists users
(
    id         bigint auto_increment
        primary key,
    password   varchar(255) not null,
    role       varchar(255) null,
    username   varchar(20)  not null,
    created_at datetime(6)  null,
    updated_at datetime(6)  null,
    create_at  datetime(6)  null
);

create table if not exists equipment_borrow_requests
(
    uniqueid             bigint auto_increment
        primary key,
    create_at            datetime(6)                                                         null,
    updated_at           datetime(6)                                                         null,
    comment              text                                                                null,
    expected_return_date date                                                                not null,
    status               enum ('BORROWED', 'NOT_BORROWED', 'PARTIALLY_RETURNED', 'RETURNED') null,
    user_id              bigint                                                              not null,
    constraint FKcboqms81ux6xvuvraamhnuff8
        foreign key (user_id) references users (id)
);

create table if not exists equipment_borrow_request_details
(
    unique_id               bigint auto_increment
        primary key,
    condition_before_borrow varchar(50) null,
    quantity_borrowed       int         not null,
    borrow_request_id       bigint      not null,
    equipment_id            bigint      null,
    constraint FK4njewwac70qc0xiq7l2ggtd9o
        foreign key (borrow_request_id) references equipment_borrow_requests (uniqueid),
    constraint FKlm6tt6ly5gk4qqmwbjf3jlqg5
        foreign key (equipment_id) references equipments (id)
);

create table if not exists borrow_detail_equipment
(
    borrow_request_detail_id bigint not null,
    equipment_detail_id      bigint not null,
    constraint FK4opgtbix6ply27p0u541hhghy
        foreign key (borrow_request_detail_id) references equipment_borrow_request_details (unique_id),
    constraint FKptwvnadx454x7uh6knemflxh
        foreign key (equipment_detail_id) references equipment_details (id)
);

create table if not exists return_requests
(
    uniqueid               bigint auto_increment
        primary key,
    create_at              datetime(6) null,
    updated_at             datetime(6) null,
    comment                text        null,
    condition_after_return varchar(50) null,
    status                 varchar(20) null,
    user_id                bigint      not null,
    constraint FK6pd9hi2rbbct43io2pgcma1sh
        foreign key (user_id) references users (id)
);

create table if not exists return_request_details
(
    unique_id                bigint auto_increment
        primary key,
    condition_after_return   varchar(50) null,
    quantity_returned        int         not null,
    equipment_id             bigint      not null,
    return_id                bigint      not null,
    borrow_request_detail_id bigint      null,
    constraint FKcw3o27jre1ld76bu79t6kwptp
        foreign key (equipment_id) references equipment_details (id),
    constraint FKikibhdm3p9jver0kicgshsnif
        foreign key (equipment_id) references equipments (id),
    constraint FKiv6uqog6hm6a0p4l2bockh2od
        foreign key (return_id) references return_requests (uniqueid)
);

create table if not exists room_borrow_requests
(
    uniqueid   bigint auto_increment
        primary key,
    create_at  datetime(6) null,
    updated_at datetime(6) null,
    comment    text        null,
    room_id    bigint      not null,
    user_id    bigint      not null,
    constraint UKj8okfu0ofmqq97vlko0iqdc1m
        unique (room_id),
    constraint FKkiqgup85vl0jarthgvuci0xou
        foreign key (room_id) references rooms (unique_id),
    constraint FKmtf2qobqi6d50r1ftw6fyjlej
        foreign key (user_id) references users (id)
);

create table if not exists transactions_log
(
    transaction_id       bigint auto_increment
        primary key,
    create_at            datetime(6) null,
    updated_at           datetime(6) null,
    transaction_type     varchar(20) not null,
    equipment_request_id bigint      null,
    room_request_id      bigint      null,
    user_id              bigint      not null,
    constraint FK86doopv8ft1x7jvoqq8q8asl8
        foreign key (user_id) references users (id),
    constraint FKa53dtcl0fmxffxnolfob8kyvi
        foreign key (room_request_id) references room_borrow_requests (uniqueid),
    constraint FKoe6txnh5rpqdv3fecd0l6fx9p
        foreign key (equipment_request_id) references equipment_borrow_requests (uniqueid)
);

