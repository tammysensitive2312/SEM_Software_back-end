-- Bảng RoomBorrowRequest, đã loại bỏ trường Status
CREATE TABLE RoomBorrowRequest (
    UniqueID BIGINT PRIMARY KEY,                     -- Khóa chính, nhận diện duy nhất mỗi đơn mượn phòng
    UserID BIGINT NOT NULL,                          -- Khóa ngoại liên kết với bảng User, xác định người mượn
    Comment TEXT,                                    -- Ghi chú bổ sung nếu có
    FOREIGN KEY (UserID) REFERENCES sem_db.users(id)
);

-- Bảng EquipmentBorrowRequest, sử dụng ENUM cho trường Status
CREATE TABLE EquipmentBorrowRequest (
    UniqueID BIGINT PRIMARY KEY,                     -- Khóa chính, nhận diện duy nhất mỗi đơn mượn thiết bị
    UserID BIGINT NOT NULL,                          -- Khóa ngoại liên kết với bảng User, xác định người mượn
    Datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,    -- Thời gian tạo đơn mượn
    ExpectedReturnDate DATE NOT NULL,                -- Ngày dự kiến trả thiết bị
    Status ENUM('Not Borrowed', 'Borrowed', 'Returned', 'Partially Returned')
                DEFAULT 'Not Borrowed',                      -- Trạng thái của đơn (ENUM)
    Comment TEXT,                                    -- Ghi chú bổ sung nếu có
    FOREIGN KEY (UserID) REFERENCES sem_db.users(id)
);


CREATE TABLE EquipmentBorrowRequestDetail (
    UniqueID BIGINT PRIMARY KEY,                     -- Khóa chính cho mỗi dòng chi tiết
    BorrowRequestID BIGINT NOT NULL,                 -- Khóa ngoại liên kết với bảng EquipmentBorrowRequest
    EquipmentID BIGINT NOT NULL,                     -- Khóa ngoại liên kết với bảng EquipmentDetail, xác định thiết bị
    QuantityBorrowed INT CHECK (QuantityBorrowed > 0),  -- Số lượng thiết bị mượn
    ConditionBeforeBorrow VARCHAR(50),             -- Tình trạng thiết bị trước khi mượn (tùy chọn)
    FOREIGN KEY (BorrowRequestID) REFERENCES EquipmentBorrowRequest(UniqueID),
    FOREIGN KEY (EquipmentID) REFERENCES sem_db.equipments(id)
);

CREATE TABLE TransactionsLog (
    TransactionID BIGINT PRIMARY KEY,                     -- Khóa chính, nhận diện duy nhất mỗi giao dịch
    UserID BIGINT NOT NULL,                               -- Khóa ngoại liên kết với bảng User
    TransactionType VARCHAR(20) NOT NULL,                 -- Loại giao dịch (mượn, trả)
    RoomRequestID BIGINT NULL,                            -- ID của đơn mượn phòng
    EquipmentRequestID BIGINT NULL,                       -- ID của đơn mượn thiết bị
    TransactionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Ngày giao dịch
    FOREIGN KEY (UserID) REFERENCES sem_db.users(id),
    FOREIGN KEY (RoomRequestID) REFERENCES RoomBorrowRequest(UniqueID),
    FOREIGN KEY (EquipmentRequestID) REFERENCES EquipmentBorrowRequest(UniqueID),
                 CHECK ((RoomRequestID IS NOT NULL AND EquipmentRequestID IS NULL)
                 OR (RoomRequestID IS NULL AND EquipmentRequestID IS NOT NULL))  -- Đảm bảo chỉ một trong hai trường có giá trị
);

CREATE TABLE ReturnRequest (
    UniqueID BIGINT PRIMARY KEY AUTO_INCREMENT,           -- Khóa chính cho mỗi đơn trả thiết bị
    UserID BIGINT NOT NULL,                               -- Khóa ngoại liên kết với bảng User, xác định người trả thiết bị
    Datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- Thời gian thực tế trả thiết bị
    Status VARCHAR(20) DEFAULT 'chờ duyệt',               -- Trạng thái đơn trả (chờ duyệt, đã duyệt, cần xử lý)
    Comment TEXT,                                         -- Ghi chú bổ sung nếu có
    Condition_after_return VARCHAR(50),                   -- Tình trạng chung sau khi trả (bình thường, hư hỏng, cần bảo trì)
    FOREIGN KEY (UserID) REFERENCES sem_db.users(id)
);

CREATE TABLE ReturnRequestDetail (
    UniqueID BIGINT PRIMARY KEY AUTO_INCREMENT,           -- Khóa chính cho mỗi chi tiết trả
    ReturnID BIGINT NOT NULL,                             -- Khóa ngoại liên kết với bảng ReturnRequest, xác định đơn trả thiết bị
    EquipmentID BIGINT NOT NULL,                          -- Khóa ngoại liên kết với bảng EquipmentDetail, xác định thiết bị nào được trả
    QuantityReturned INT CHECK (QuantityReturned > 0),    -- Số lượng thiết bị được trả
    ConditionAfterReturn VARCHAR(50),                     -- Tình trạng thiết bị sau khi trả (bình thường, hư hỏng, cần bảo trì)
    FOREIGN KEY (ReturnID) REFERENCES ReturnRequest(UniqueID),
    FOREIGN KEY (EquipmentID) REFERENCES sem_db.equipment_details(id)
);

