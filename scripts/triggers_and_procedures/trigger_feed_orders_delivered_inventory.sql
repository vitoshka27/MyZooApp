DELIMITER //
CREATE TRIGGER trg_feed_orders_delivered_inventory
AFTER UPDATE ON feed_orders
FOR EACH ROW
BEGIN
    IF NEW.status = 'Доставлен' AND OLD.status <> 'Доставлен' THEN
        IF EXISTS (SELECT 1 FROM feed_inventory WHERE feed_item_id = NEW.feed_item_id) THEN
            UPDATE feed_inventory
            SET quantity = quantity + NEW.ordered_quantity,
                received_date = NOW()
            WHERE feed_item_id = NEW.feed_item_id;
        ELSE
            INSERT INTO feed_inventory (feed_item_id, quantity, received_date)
            VALUES (NEW.feed_item_id, NEW.ordered_quantity, NOW());
        END IF;
    END IF;
END;//
DELIMITER ; 