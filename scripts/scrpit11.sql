WITH offspring AS (
  SELECT 
    a.id AS animal_id,
    (SELECT COUNT(*) 
      FROM (
        SELECT parent1_id AS pid FROM animals WHERE parent1_id = a.id
        UNION ALL
        SELECT parent2_id AS pid FROM animals WHERE parent2_id = a.id
      ) t
    ) AS offspring_count
  FROM animals a
),

last_medical AS (
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
),

vaccinations AS (
  SELECT 
    av.animal_id,
    GROUP_CONCAT(
      CONCAT(v.name, ' (', av.vaccination_date, ')') 
      ORDER BY av.vaccination_date 
      SEPARATOR '; '
    ) AS all_vaccines
  FROM animal_vaccinations av
  JOIN vaccines v ON av.vaccine_id = v.id
  GROUP BY av.animal_id
),

diseases AS (
  SELECT 
    ad.animal_id,
    GROUP_CONCAT(
      CONCAT(d.name, ' (', ad.diagnosed_date, ')') 
      ORDER BY ad.diagnosed_date 
      SEPARATOR '; '
    ) AS all_diseases
  FROM animal_diseases ad
  JOIN diseases d ON ad.disease_id = d.id
  GROUP BY ad.animal_id
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
  lm.weight,
  lm.height,
  vac.all_vaccines AS vaccinations,
  dis.all_diseases AS diseases,
  COUNT(*) OVER () AS total_animals
FROM animals a
JOIN species sp ON a.species_id   = sp.id
JOIN enclosures e ON a.enclosure_id = e.id
LEFT JOIN last_medical lm ON a.id = lm.animal_id
LEFT JOIN offspring o ON a.id = o.animal_id
LEFT JOIN vaccinations vac ON a.id = vac.animal_id
LEFT JOIN diseases dis ON a.id = dis.animal_id
WHERE 1 = 1
  -- AND a.species_id = 2
  -- AND a.id = 4
  -- AND a.enclosure_id = 4
ORDER BY a.name;