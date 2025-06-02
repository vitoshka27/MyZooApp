WITH offspring AS (
  SELECT a.id AS animal_id,
 (SELECT COUNT(*) FROM (
	  SELECT parent1_id FROM animals WHERE parent1_id = a.id
	  UNION ALL
	  SELECT parent2_id FROM animals WHERE parent2_id = a.id
	) AS t
  )AS offspring_count
  FROM animals a
)

SELECT 
    a.name,
    a.gender,
    a.birth_date,
    get_years_diff(a.birth_date) AS age,
    a.arrival_date,
    get_years_diff(a.arrival_date) AS years_in_zoo,
    sp.type_name AS species,
    e.name AS enclosure,
    o.offspring_count,
    COUNT(*) OVER () AS total_animals
FROM animals a
JOIN species sp ON a.species_id = sp.id
JOIN enclosures e ON a.enclosure_id = e.id
JOIN offspring o ON a.id = o.animal_id
WHERE 1 = 1
	-- AND a.id IN (SELECT animal_id FROM animal_vaccinations WHERE vaccine_id = 1) 
    -- AND a.id IN (SELECT animal_id FROM animal_diseases WHERE disease_id = 1)
	-- AND get_years_diff(a.arrival_date) BETWEEN 2 AND 10
	-- AND a.gender = 'М'
	-- AND get_years_diff(a.birth_date) BETWEEN 5 AND 15
	-- AND o.offspring_count >= 1
ORDER BY 
-- age DESC,
-- years_in_zoo DESC,
-- o.offspring_count DESC,
a.name;
