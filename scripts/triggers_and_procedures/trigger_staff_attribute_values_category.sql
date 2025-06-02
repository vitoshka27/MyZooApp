DELIMITER //
CREATE TRIGGER trg_staff_attribute_values_category_insert
BEFORE INSERT ON staff_attribute_values
FOR EACH ROW
BEGIN
    DECLARE cat_id INT;
    DECLARE cnt INT;
    SELECT category_id INTO cat_id FROM staff WHERE id = NEW.staff_id;
    SELECT COUNT(*) INTO cnt FROM category_attributes WHERE id = NEW.attribute_id AND category_id = cat_id;
    IF cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Атрибут не определён для категории сотрудника';
    END IF;
END;//

CREATE TRIGGER trg_staff_attribute_values_category_update
BEFORE UPDATE ON staff_attribute_values
FOR EACH ROW
BEGIN
    DECLARE cat_id INT;
    DECLARE cnt INT;
    SELECT category_id INTO cat_id FROM staff WHERE id = NEW.staff_id;
    SELECT COUNT(*) INTO cnt FROM category_attributes WHERE id = NEW.attribute_id AND category_id = cat_id;
    IF cnt = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Атрибут не определён для категории сотрудника';
    END IF;
END;//
DELIMITER ; 