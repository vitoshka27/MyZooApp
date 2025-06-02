DELIMITER //
CREATE TRIGGER trg_animals_need_warm_insert
BEFORE INSERT ON animals
FOR EACH ROW
BEGIN
    DECLARE need_warm CHAR(1);
    DECLARE is_warm CHAR(1);
    DECLARE cur_month INT;
    SET cur_month = MONTH(CURDATE());
    SELECT s.need_warm INTO need_warm FROM species s WHERE s.id = NEW.species_id;
    SELECT e.is_warm INTO is_warm FROM enclosures e WHERE e.id = NEW.enclosure_id;
    IF need_warm = 'Y' AND is_warm <> 'Y' AND (cur_month = 12 OR cur_month = 1 OR cur_month = 2) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Зимой животное требует отапливаемого помещения';
    END IF;
END;//

CREATE TRIGGER trg_animals_need_warm_update
BEFORE UPDATE ON animals
FOR EACH ROW
BEGIN
    DECLARE need_warm CHAR(1);
    DECLARE is_warm CHAR(1);
    DECLARE cur_month INT;
    SET cur_month = MONTH(CURDATE());
    SELECT s.need_warm INTO need_warm FROM species s WHERE s.id = NEW.species_id;
    SELECT e.is_warm INTO is_warm FROM enclosures e WHERE e.id = NEW.enclosure_id;
    IF need_warm = 'Y' AND is_warm <> 'Y' AND (cur_month = 12 OR cur_month = 1 OR cur_month = 2) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Зимой животное требует отапливаемого помещения';
    END IF;
END;//
DELIMITER ; 