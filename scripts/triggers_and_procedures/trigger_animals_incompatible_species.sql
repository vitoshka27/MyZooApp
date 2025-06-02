DELIMITER //
CREATE TRIGGER trg_animals_incompatible_species_insert
BEFORE INSERT ON animals
FOR EACH ROW
BEGIN
    CALL check_animals_compatibility(NEW.species_id, NEW.enclosure_id);
END;//

CREATE TRIGGER trg_animals_incompatible_species_update
BEFORE UPDATE ON animals
FOR EACH ROW
BEGIN
    CALL check_animals_compatibility(NEW.species_id, NEW.enclosure_id);
END;//
DELIMITER ; 