SHOW CREATE TABLE sem_db.equipments;
SHOW CREATE TABLE sem_db.equipment_details;
/*
ALTER TABLE equipment DROP CONSTRAINT equipment_chk_1;

ALTER TABLE equipment
    ADD CONSTRAINT equipment_chk_1 CHECK (category IN ('INFORMATION_TECHNOLOGY_EQUIPMENT', 'LABORATORY_EQUIPMENT', 'SPORTS_EQUIPMENT', 'TEACHING_EQUIPMENT'));
 */
SELECT e.category, ed.id, ed.code, ed.description, ed.status,ed.operating_hours, ed.purchase_date, r.room_name, r.type
FROM sem_db.equipment_details ed
         INNER JOIN sem_db.rooms r ON ed.room_id = r.unique_id
         INNER JOIN sem_db.equipments e ON ed.room_id = e.id
ORDER BY r.room_name
LIMIT 10 OFFSET 0;


SELECT r.unique_id, r.room_name, r.type, r.capacity, r.status
FROM sem_db.rooms r
WHERE r.type = 'phòng thí nghiệm'
  AND r.status = 'available'
  AND NOT EXISTS (
    SELECT 1
    FROM sem_db.room_schedules rs
    WHERE rs.room_id = r.unique_id
      AND ('2024-10-21 09:45' < rs.end_time AND '2024-10-21 9:00' > rs.start_time)
);

