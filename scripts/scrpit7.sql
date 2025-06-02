WITH all_incompatible AS (
    SELECT species1_id AS sp1, species2_id AS sp2 FROM incompatible_species
    UNION
    SELECT species2_id AS sp1, species1_id AS sp2 FROM incompatible_species
)

SELECT 
    a.name,
    a.gender,
    a.birth_date,
    get_years_diff(a.birth_date) AS age,
    get_years_diff(a.arrival_date) AS years_in_zoo,
    sp.type_name AS species,
    e.name AS enclosure,
    COUNT(*) OVER () AS total_animals
FROM animals a
JOIN species sp ON a.species_id = sp.id
JOIN enclosures e ON a.enclosure_id = e.id
WHERE 1 = 1
    -- AND sp.need_warm = 'Y'
    -- AND a.species_id NOT IN (SELECT sp2 FROM all_incompatible WHERE sp1 = 1)
ORDER BY a.name;
