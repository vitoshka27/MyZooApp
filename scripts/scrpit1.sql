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
    COUNT(*) OVER () AS total_employees
FROM staff s
JOIN staff_categories sc ON s.category_id = sc.id
WHERE 1 = 1
  -- AND s.category_id = 1
  -- AND s.gender = 'М'
  -- AND salary BETWEEN 30000 AND 50000
  -- AND get_years_diff(s.hire_date) BETWEEN 5 AND 10
  -- AND get_years_diff(s.birth_date) BETWEEN 30 AND 40
ORDER BY 
-- years_worked DESC,
-- age DESC,
-- salary DESC,
s.last_name, 
s.first_name;
