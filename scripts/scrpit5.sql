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
WHERE sp.need_warm = 'Y'
  -- AND a.species_id = 1
  -- AND get_years_diff(a.birth_date) BETWEEN 3 AND 10
ORDER BY 
-- age DESC,
-- years_in_zoo DESC,
a.name;
 