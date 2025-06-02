DELIMITER //
CREATE TRIGGER trg_feed_inventory_after_feeding_insert
AFTER INSERT ON animal_feedings
FOR EACH ROW
BEGIN
    DECLARE feed_item_id INT;
    SELECT fi.id INTO feed_item_id
    FROM feed_items fi
    WHERE fi.id = NEW.feed_item_id;
    
    IF EXISTS (
        SELECT 1 
        FROM feed_inventory 
        WHERE feed_item_id = feed_item_id 
        AND quantity >= NEW.quantity
    ) THEN
        UPDATE feed_inventory
        SET quantity = quantity - NEW.quantity
        WHERE feed_item_id = feed_item_id;
    ELSE
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Недостаточное количество корма на складе';
    END IF;
END;//

CREATE TRIGGER trg_feed_inventory_after_feeding_update
AFTER UPDATE ON animal_feedings
FOR EACH ROW
BEGIN
    DECLARE feed_item_id INT;
    SELECT fi.id INTO feed_item_id
    FROM feed_items fi
    WHERE fi.id = NEW.feed_item_id;
    
    IF NEW.quantity != OLD.quantity THEN
        UPDATE feed_inventory
        SET quantity = quantity + OLD.quantity
        WHERE feed_item_id = feed_item_id;
        
        IF EXISTS (
            SELECT 1 
            FROM feed_inventory 
            WHERE feed_item_id = feed_item_id 
            AND quantity >= NEW.quantity
        ) THEN
            UPDATE feed_inventory
            SET quantity = quantity - NEW.quantity
            WHERE feed_item_id = feed_item_id;
        ELSE
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Недостаточное количество корма на складе';
        END IF;
    END IF;
END;//
DELIMITER ; 