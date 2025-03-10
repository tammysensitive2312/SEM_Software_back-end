use sem_db;

create table documents (
    id bigint primary key auto_increment,
    file_id varchar(100) not null unique,
    file_name varchar(255),
    file_path varchar(255),
    file_size bigint,
    metadata json,
    created_at timestamp default current_timestamp
);

create table document_versions (
    id bigint primary key auto_increment,
    document_id bigint not null,
    revision_id varchar(100),
    updated_at timestamp,
    size bigint,
    foreign key (document_id) references documents(id)
)