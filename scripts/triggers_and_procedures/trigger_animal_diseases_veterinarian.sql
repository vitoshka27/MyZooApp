DELIMITER //
CREATE TRIGGER trg_animal_diseases_veterinarian_insert
BEFORE INSERT ON animal_diseases
FOR EACH ROW
BEGIN
    DECLARE cat_id INT;
    DECLARE cnt INT;
    SELECT category_id INTO cat_id FROM staff WHERE id = NEW.veterinarian_id;
    IF cat_id <> 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Сотрудник не является ветеринаром';
    END IF;
    SELECT COUNT(*) INTO cnt FROM animal_caretakers
    WHERE animal_id = NEW.animal_id AND staff_id = NEW.veterinarian_id
          AND (end_date IS NULL OR end_date >= NEW.diagnosed_date);
    IF cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ветеринар не ухаживает за этим животным';
    END IF;
END;//

CREATE TRIGGER trg_animal_diseases_veterinarian_update
BEFORE UPDATE ON animal_diseases
FOR EACH ROW
BEGIN
    DECLARE cat_id INT;
    DECLARE cnt INT;
    SELECT category_id INTO cat_id FROM staff WHERE id = NEW.veterinarian_id;
    IF cat_id <> 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Сотрудник не является ветеринаром';
    END IF;
    SELECT COUNT(*) INTO cnt FROM animal_caretakers
    WHERE animal_id = NEW.animal_id AND staff_id = NEW.veterinarian_id
          AND (end_date IS NULL OR end_date >= NEW.diagnosed_date);
    IF cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ветеринар не ухаживает за этим животным';
    END IF;
END;//
DELIMITER ; 