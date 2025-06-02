DELIMITER //
CREATE PROCEDURE get_all_offspring_count(IN animal_id INT, OUT offspring_count INT)
BEGIN
	DECLARE new_rows INT DEFAULT 1;
	DECLARE id_list TEXT;
	DECLARE offspring_list TEXT DEFAULT '';
	SET offspring_count = 0;
    DROP TEMPORARY TABLE IF EXISTS tmp_offspring;
    DROP TEMPORARY TABLE IF EXISTS tmp_new;
    CREATE TEMPORARY TABLE tmp_offspring (id INT PRIMARY KEY);
    CREATE TEMPORARY TABLE tmp_new (id INT PRIMARY KEY);

    INSERT INTO tmp_offspring (id)
    SELECT id FROM animals WHERE parent1_id = animal_id OR parent2_id = animal_id;

    SELECT GROUP_CONCAT(id) INTO offspring_list FROM tmp_offspring;

    WHILE new_rows > 0 DO
        TRUNCATE TABLE tmp_new;
        INSERT IGNORE INTO tmp_new (id) SELECT id FROM tmp_offspring;

        SELECT GROUP_CONCAT(id) INTO id_list FROM tmp_new;

        SET new_rows = 0;

        IF id_list IS NOT NULL THEN
            INSERT IGNORE INTO tmp_offspring (id)
            SELECT a.id
            FROM animals a
            WHERE FIND_IN_SET(a.parent1_id, id_list)
              AND (offspring_list IS NULL OR FIND_IN_SET(a.id, offspring_list) = 0);
            SET new_rows = new_rows + ROW_COUNT();

            INSERT IGNORE INTO tmp_offspring (id)
            SELECT a.id
            FROM animals a
            WHERE FIND_IN_SET(a.parent2_id, id_list)
              AND (offspring_list IS NULL OR FIND_IN_SET(a.id, offspring_list) = 0);
            SET new_rows = new_rows + ROW_COUNT();

            SELECT GROUP_CONCAT(id) INTO offspring_list FROM tmp_offspring;
        END IF;
    END WHILE;

    SELECT COUNT(*) INTO offspring_count FROM tmp_offspring;
    DROP TEMPORARY TABLE tmp_offspring;
    DROP TEMPORARY TABLE tmp_new;
END;//
DELIMITER ; 