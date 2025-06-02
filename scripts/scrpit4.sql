WITH last_medical AS (
  SELECT 
    m1.animal_id,
    m1.weight,
    m1.height
  FROM animal_medical_records m1
  JOIN (
    SELECT animal_id, MAX(record_date) AS max_date
    FROM animal_medical_records
    GROUP BY animal_id
  ) m2 
    ON m1.animal_id = m2.animal_id 
   AND m1.record_date = m2.max_date
)

SELECT 
    a.name,
    a.gender,
    a.birth_date,
    get_years_diff(a.birth_date) AS age,
    sp.type_name AS species,
    e.name AS enclosure,
    lm.weight,
    lm.height,
    COUNT(*) OVER () AS total_animals
FROM animals a
JOIN species sp ON a.species_id = sp.id
JOIN enclosures e ON a.enclosure_id = e.id
LEFT JOIN last_medical lm ON a.id = lm.animal_id
WHERE 1 = 1
  -- AND sp.id = 6
  -- AND e.id = 2
  -- AND a.gender = 'М'
  -- AND get_years_diff(a.birth_date) BETWEEN 3 AND 10
  -- AND am.weight BETWEEN 50 AND 200
  -- AND am.height BETWEEN 0.5 AND 1.15
ORDER BY
-- age DESC,
-- am.weight DESC,
-- am.height DESC,
a.name;
