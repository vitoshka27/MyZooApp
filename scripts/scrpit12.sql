WITH selection AS (
  SELECT
    a.name,
    a.gender,
    a.species_id,
    s.type_name as species,
    get_years_diff(a.birth_date) AS age
  FROM animals a
  JOIN species s
    ON a.species_id = s.id
  WHERE
    get_years_diff(a.birth_date) >= s.puberty_age
    -- AND a.species_id = 4
)
SELECT
  sel.name,
  sel.gender,
  sel.species,
  sel.age,
  COUNT(*) OVER () AS total_animals
FROM selection sel
WHERE EXISTS (
  SELECT 1
  FROM selection p
  WHERE p.species_id = sel.species_id
    AND p.gender <> sel.gender
)
ORDER BY
  sel.species,
  sel.name;