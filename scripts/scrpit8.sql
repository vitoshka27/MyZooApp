SELECT
    fs.name,
    fs.phone,
    fs.address,
    COUNT(DISTINCT fo.id) AS order_count,
    SUM(fo.ordered_quantity) AS total_ordered_quantity,
    AVG(fo.price) AS avg_price,
    COUNT(*) OVER () AS total_suppliers
FROM feed_suppliers fs
JOIN feed_orders fo ON fs.id = fo.feed_supplier_id
WHERE 1=1
    -- AND fs.id IN (
	--     SELECT sft.supplier_id FROM supplier_feed_types sft WHERE sft.feed_type_id = 1
        -- AND fo.order_date BETWEEN '2024-02-01' AND '2024-03-01'
		-- AND fo.ordered_quantity BETWEEN 10 AND 100
		-- AND fo.price BETWEEN 1000 AND 5000
		-- AND fo.delivery_date BETWEEN '2024-02-01' AND '2024-03-01'
    -- )
GROUP BY fs.id, fs.name, fs.phone, fs.address
ORDER BY fs.name;