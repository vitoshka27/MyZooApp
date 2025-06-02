DELIMITER //
CREATE PROCEDURE check_animals_compatibility(
    IN new_species_id INT,
    IN new_enclosure_id INT
)
BEGIN
    DECLARE cnt INT;
    SELECT COUNT(*) INTO cnt
    FROM animals a
    JOIN enclosures e ON a.enclosure_id = e.id
    JOIN enclosure_neighbors n 
      ON ( (n.enclosure1_id = new_enclosure_id AND n.enclosure2_id = e.id)
        OR (n.enclosure2_id = new_enclosure_id AND n.enclosure1_id = e.id) )
    JOIN incompatible_species i 
      ON ( (i.species1_id = new_species_id AND i.species2_id = a.species_id)
        OR (i.species2_id = new_species_id AND i.species1_id = a.species_id) )
    WHERE a.enclosure_id IS NOT NULL;
    IF cnt > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'В соседних клетках есть несовместимые виды';
    END IF;
END;//
DELIMITER ; 