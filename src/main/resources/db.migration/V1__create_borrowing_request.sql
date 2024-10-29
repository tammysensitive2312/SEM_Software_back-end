CREATE table if not exists BorrowRequest (
    unique_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_type ENUM('EQUIPMENT', 'ROOM'),
    status ENUM('NOT BORROWED', 'BORROWED', 'RETURNED', 'PARTIAL RETURNED'),
    comment TEXT,
    user_id BIGINT NULL,
    Foreign Key `fklinktouser` (user_id) REFERENCES sem_db.users (id)
)