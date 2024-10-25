CREATE TABLE equipment_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    purchaseDate VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    code VARCHAR(255) NOT NULL UNIQUE,
    status ENUM('USABLE', 'BROKEN', 'OCCUPIED') DEFAULT 'AVAILABLE',
    operatingHours INT,
    equipment_id INT NOT NULL,
    room_id INT NOT NULL,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    FOREIGN KEY (room_id) REFERENCES room(id)
);