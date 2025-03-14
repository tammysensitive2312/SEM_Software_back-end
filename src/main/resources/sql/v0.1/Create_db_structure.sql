create table if not exists equipments
(
    id              bigint auto_increment
        primary key,
    create_at       datetime(6)                                                                                                                                           null,
    updated_at      datetime(6)                                                                                                                                           null,
    broken_quantity int                                                                                                                                                   not null,
    category        enum ('ELECTRIC_EQUIPMENT', 'INFORMATION_TECHNOLOGY_EQUIPMENT', 'LABORATORY_EQUIPMENT', 'OFFICE_EQUIPMENT', 'SPORTS_EQUIPMENT', 'TEACHING_EQUIPMENT') null,
    code            varchar(255)                                                                                                                                          not null,
    equipment_name  varchar(255)                                                                                                                                          not null,
    in_use_quantity int                                                                                                                                                   not null,
    total_quantity  int                                                                                                                                                   not null,
    usable_quantity int                                                                                                                                                   not null,
    version         int                                                                                                                                                   not null,
    constraint UK6j9l7rrrh7w5yqrmuagg5dg0q
        unique (equipment_name),
    constraint UK7fenrquevt41j1726xy6omw30
        unique (code)
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

create table if not exists notifications
(
    id         bigint auto_increment
        primary key,
    create_at  datetime(6)  null,
    updated_at datetime(6)  null,
    is_read    bit          not null,
    message    varchar(255) null,
    read_at    datetime(6)  null,
    type       tinyint      null,
    check (`type` between 0 and 2)
);

create table if not exists notification_recipients
(
    notification_id bigint not null,
    recipient_id    bigint null,
    constraint FKiuf5qgbttjq6ry57u1dni7qn4
        foreign key (notification_id) references notifications (id)
);

create table if not exists refresh_token_seq
(
    next_val bigint null
);

create table if not exists rooms
(
    unique_id bigint auto_increment
        primary key,
    capacity  int                                                                   null,
    room_name varchar(255)                                                            not null,
    status    enum ('AVAILABLE', 'BROKEN', 'IN_USE')                                  null,
    type      enum ('CLASSROOM', 'LABORATORY', 'MEETING_ROOM', 'OFFICE', 'WAREHOUSE') null,
    constraint UK36daphag00mnxmwforqperdnb
        unique (room_name)
);

create table if not exists equipment_details
(
    id            bigint auto_increment
        primary key,
    create_at     datetime(6)                           null,
    updated_at    datetime(6)                           null,
    description   varchar(255)                          null,
    purchase_date date                                  null,
    serial_number varchar(255)                          not null,
    status        enum ('BROKEN', 'OCCUPIED', 'USABLE') null,
    equipment_id  bigint                                not null,
    room_id       bigint                                null,
    constraint UK811rio19pfgeqay3ka4mnpg2n
        unique (serial_number),
    constraint FKfdc6dt2e2n7he3o4htog4kha
        foreign key (equipment_id) references equipments (id),
    constraint FKg71fikpmsnluvg6bx2855iynp
        foreign key (room_id) references rooms (unique_id)
);

create table if not exists room_schedules
(
    unique_id  bigint auto_increment
        primary key,
    end_time   datetime(6)  null,
    start_time datetime(6)  null,
    user       varchar(255) null,
    version    int          null,
    room_id    bigint       null,
    constraint FKl0fj6n9kh38cf3xkmll8ld6wh
        foreign key (room_id) references rooms (unique_id)
);

create table if not exists users
(
    id         bigint auto_increment
        primary key,
    create_at  datetime(6)  null,
    updated_at datetime(6)  null,
    password   varchar(255) not null,
    role       varchar(255) null,
    username   varchar(255) not null,
    email      varchar(255) not null
);

create table if not exists equipment_borrow_requests
(
    uniqueid             bigint auto_increment
        primary key,
    create_at            datetime(6)                                                         null,
    updated_at           datetime(6)                                                         null,
    comment              text                                                                null,
    expected_return_date date                                                                null,
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

create table if not exists refresh_token
(
    id          bigint       not null
        primary key,
    expiry_date datetime(6)  not null,
    token       varchar(255) not null,
    user_id     bigint       null,
    constraint UKf95ixxe7pa48ryn1awmh2evt7
        unique (user_id),
    constraint UKr4k4edos30bx9neoq81mdvwph
        unique (token),
    constraint FKjtx87i0jvq2svedphegvdwcuy
        foreign key (user_id) references users (id)
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

create table if not exists user_preference
(
    id                  bigint auto_increment
        primary key,
    email_notification  bit    not null,
    in_app_notification bit    not null,
    push_notification   bit    not null,
    user_id             bigint null,
    constraint UKs5oeayykfc7bpkpdwyrffwcqx
        unique (user_id),
    constraint FK7mgxw6j2m7uvuk3svr9vsar8p
        foreign key (user_id) references users (id)
);

