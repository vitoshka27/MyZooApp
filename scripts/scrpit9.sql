SELECT
    fi.name AS feed_item,
    ft.name AS feed_type,
    SUM(fp.quantity) AS total_produced,
    COUNT(*) OVER () AS total_feed_items
FROM feed_items fi
JOIN feed_production fp ON fi.id = fp.feed_item_id
JOIN feed_types ft ON fi.feed_type = ft.id
LEFT JOIN feed_orders fo ON fi.id = fo.feed_item_id
WHERE fo.id IS NULL
GROUP BY
    fi.id,
    fi.name,
    ft.name
ORDER BY
    fi.name;