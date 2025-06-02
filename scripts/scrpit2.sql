SELECT 
    s.last_name,
    s.first_name,
    s.middle_name,
    s.gender,
    s.birth_date,
    get_years_diff(s.birth_date) AS age,
    s.hire_date,
    get_years_diff(s.hire_date) AS years_worked,
    s.salary,
    sc.name AS category,
    a.id AS animal_id,
    a.name AS animal_name,
    ac.start_date AS start_date,
    ac.end_date AS end_date,
    COUNT(*) OVER () AS total_caretakers
FROM staff s
JOIN staff_categories sc ON s.category_id = sc.id
JOIN animal_caretakers ac ON s.id = ac.staff_id
JOIN animals a ON ac.animal_id = a.id
WHERE ac.animal_id = 1 
	-- AND ac.start_date >= '2016-01-01'
    -- AND (ac.end_date <= '2020-01-01' OR ac.end_date is NULL)
    -- AND s.gender = 'М'
ORDER BY 
-- years_worked DESC,
-- age DESC,
-- salary DESC,
s.last_name, 
s.first_name;