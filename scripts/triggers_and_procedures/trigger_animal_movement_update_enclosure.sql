DELIMITER //
CREATE TRIGGER trg_animal_movement_update_enclosure_insert
AFTER INSERT ON animal_movements
FOR EACH ROW
BEGIN
    DECLARE latest_movement_date DATE;
    SELECT MAX(movement_date) INTO latest_movement_date
    FROM animal_movements
    WHERE animal_id = NEW.animal_id;
    
    IF NEW.movement_date = latest_movement_date THEN
        UPDATE animals
        SET enclosure_id = NEW.new_enclosure_id
        WHERE id = NEW.animal_id;
    END IF;
END;//

CREATE TRIGGER trg_animal_movement_update_enclosure_update
AFTER UPDATE ON animal_movements
FOR EACH ROW
BEGIN
    DECLARE latest_movement_date DATE;
    
    SELECT MAX(movement_date) INTO latest_movement_date
    FROM animal_movements
    WHERE animal_id = NEW.animal_id;
    
    IF NEW.movement_date = latest_movement_date THEN
        UPDATE animals
        SET enclosure_id = NEW.new_enclosure_id
        WHERE id = NEW.animal_id;
    END IF;
END;//
DELIMITER ; 