DELIMITER //
CREATE TRIGGER trg_feed_suppliers_phone_format_insert
BEFORE INSERT ON feed_suppliers
FOR EACH ROW
BEGIN
    DECLARE cleaned VARCHAR(20);
    SET cleaned = REGEXP_REPLACE(NEW.phone, '[^0-9]', '');
    IF LEFT(cleaned, 1) = '8' THEN
        SET cleaned = CONCAT('7', SUBSTRING(cleaned, 2));
    END IF;
    IF LEFT(cleaned, 1) = '7' AND LENGTH(cleaned) = 11 THEN
        SET NEW.phone = CONCAT('+7-', SUBSTRING(cleaned, 2, 3), '-', SUBSTRING(cleaned, 5, 3), '-', SUBSTRING(cleaned, 8, 4));
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Телефон поставщика должен быть российским номером (+7-XXX-XXX-XXXX)';
    END IF;
END;//

CREATE TRIGGER trg_feed_suppliers_phone_format_update
BEFORE UPDATE ON feed_suppliers
FOR EACH ROW
BEGIN
    DECLARE cleaned VARCHAR(20);
    SET cleaned = REGEXP_REPLACE(NEW.phone, '[^0-9]', '');
    IF LEFT(cleaned, 1) = '8' THEN
        SET cleaned = CONCAT('7', SUBSTRING(cleaned, 2));
    END IF;
    IF LEFT(cleaned, 1) = '7' AND LENGTH(cleaned) = 11 THEN
        SET NEW.phone = CONCAT('+7-', SUBSTRING(cleaned, 2, 3), '-', SUBSTRING(cleaned, 5, 3), '-', SUBSTRING(cleaned, 8, 4));
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Телефон поставщика должен быть российским номером (+7-XXX-XXX-XXXX)';
    END IF;
END;//
DELIMITER ; 