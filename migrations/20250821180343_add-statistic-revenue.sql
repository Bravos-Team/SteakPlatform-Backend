CREATE OR REPLACE FUNCTION getTotalRevenueByDay(year INTEGER, month INTEGER)
RETURNS TABLE (
    day INTEGER,
    revenue NUMERIC(12, 2)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT EXTRACT(DAY FROM TO_TIMESTAMP(o.created_at / 1000))::integer AS day,
               COALESCE(SUM(od.price), 0) AS revenue
        FROM orders o
        LEFT JOIN order_details od ON o.id = od.order_id
        WHERE o.status = 0
          AND EXTRACT(YEAR FROM TO_TIMESTAMP(o.created_at / 1000)) = year
          AND EXTRACT(MONTH FROM TO_TIMESTAMP(o.created_at / 1000)) = month
        GROUP BY day
        ORDER BY day;
END;
$$;

CREATE OR REPLACE FUNCTION getTotalRevenueByMonth(year INTEGER)
RETURNS TABLE (
    month INTEGER,
    revenue NUMERIC(12, 2)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT EXTRACT(MONTH FROM TO_TIMESTAMP(o.created_at / 1000))::integer AS month,
               COALESCE(SUM(od.price), 0) AS revenue
        FROM orders o
        LEFT JOIN order_details od ON o.id = od.order_id
        WHERE o.status = 0
          AND EXTRACT(YEAR FROM TO_TIMESTAMP(o.created_at / 1000)) = year
        GROUP BY month
        ORDER BY month;
END;
$$;

CREATE OR REPLACE FUNCTION getTotalRevenueByYear()
RETURNS TABLE (
    year INTEGER,
    revenue NUMERIC(12, 2)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        SELECT EXTRACT(YEAR FROM TO_TIMESTAMP(o.created_at / 1000))::integer AS year,
               COALESCE(SUM(od.price), 0) AS revenue
        FROM orders o
        LEFT JOIN order_details od ON o.id = od.order_id
        WHERE o.status = 0
        GROUP BY year
        ORDER BY year;
END;
$$;
