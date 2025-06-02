WITH animal_menu_info AS (
    SELECT DISTINCT
        dfm.animal_id,
        adr.feed_type_id,
        adr.season,
        adr.age_group
    FROM daily_feeding_menu dfm
    JOIN animal_diet_requirements adr
      ON dfm.diet_id = adr.id
    WHERE 1 = 1
	    -- AND adr.feed_type_id = 1
		-- AND (adr.season = 'Годовой' OR adr.season = 'Весна')
		-- AND adr.age_group    = 'Взрослый'
)

SELECT
    a.name,
    a.gender,
    a.birth_date,
    get_years_diff(a.birth_date) AS age,
    sp.type_name AS species,
    e.name AS enclosure,
    ft.name     AS feed_type,
    ami.season  AS season,
    ami.age_group AS age_group,
    COUNT(*) OVER () AS total_animals
FROM animals a
JOIN animal_menu_info ami
  ON a.id = ami.animal_id
JOIN feed_types ft
  ON ami.feed_type_id = ft.id
JOIN species sp ON a.species_id = sp.id
JOIN enclosures e ON a.enclosure_id = e.id
WHERE 1 = 1
    -- AND sp.id = 2
ORDER BY a.name;
