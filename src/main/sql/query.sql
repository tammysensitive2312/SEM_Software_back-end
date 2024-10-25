SHOW CREATE TABLE equipment;
/*
ALTER TABLE equipment DROP CONSTRAINT equipment_chk_1;

ALTER TABLE equipment
    ADD CONSTRAINT equipment_chk_1 CHECK (category IN ('INFORMATION_TECHNOLOGY_EQUIPMENT', 'LABORATORY_EQUIPMENT', 'SPORTS_EQUIPMENT', 'TEACHING_EQUIPMENT'));
 */
SELECT e.category, ed.id, ed.code, ed.description, ed.current_status,ed.operating_hours, ed.purchase_date, r.number, r.type
FROM equipment_detail ed
         INNER JOIN room r ON ed.room_id = r.unique_id
         INNER JOIN equipment e ON ed.room_id = e.id
ORDER BY r.number ASC
    LIMIT 10 OFFSET 0;


SELECT r.unique_id, r.number, r.type, r.capacity, r.room_condition
FROM room r
WHERE r.type = 'phòng thí nghiệm'
  AND r.room_condition = 'available'
  AND NOT EXISTS (
    SELECT 1
    FROM room_schedules rs
    WHERE rs.room_id = r.unique_id
      AND ('2024-10-21 09:45' < rs.end_time AND '2024-10-21 9:00' > rs.start_time)
);

