SELECT 
    ze.partner_zoo,
    COUNT(*) AS exchange_count,
    COUNT(*) OVER () AS total_zoos
FROM zoo_exchanges ze
JOIN animals a ON ze.animal_id = a.id
WHERE 1 = 1
	-- AND a.species_id = 
GROUP BY ze.partner_zoo
ORDER BY
	ze.partner_zoo;
