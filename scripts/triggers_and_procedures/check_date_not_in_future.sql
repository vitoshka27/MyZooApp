 DELIMITER //
CREATE PROCEDURE check_date_not_in_future(
    IN some_date DATE,
    IN err_message VARCHAR(255)
)
BEGIN
    IF some_date > CURDATE() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = err_message;
    END IF;
END;//
DELIMITER ;
