DELIMITER //
CREATE TRIGGER trg_feed_production_inventory
AFTER INSERT ON feed_production
FOR EACH ROW
BEGIN
    DECLARE cur_received_date TIMESTAMP;
    IF EXISTS (SELECT 1 FROM feed_inventory WHERE feed_item_id = NEW.feed_item_id) THEN
        SELECT received_date INTO cur_received_date FROM feed_inventory WHERE feed_item_id = NEW.feed_item_id;
        IF NEW.production_date > cur_received_date THEN
            UPDATE feed_inventory
            SET quantity = quantity + NEW.quantity,
                received_date = NEW.production_date
            WHERE feed_item_id = NEW.feed_item_id;
        ELSE
            UPDATE feed_inventory
            SET quantity = quantity + NEW.quantity
            WHERE feed_item_id = NEW.feed_item_id;
        END IF;
    ELSE
        INSERT INTO feed_inventory (feed_item_id, quantity, received_date)
        VALUES (NEW.feed_item_id, NEW.quantity, NEW.production_date);
    END IF;
END;//
DELIMITER ; 