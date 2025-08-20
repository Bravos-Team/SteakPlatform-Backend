CREATE OR REPLACE FUNCTION proc_weekly_trending(as_of_time TIMESTAMP with time zone DEFAULT NOW())
    RETURNS TABLE(
                     game_id BIGINT,
                     peak_concurrent BIGINT,
                     avg_concurrent NUMERIC(12,2),
                     growth_rate NUMERIC(6,2),
                     trending_score NUMERIC(12,2)
                 )
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        WITH peak AS (
            SELECT p.game_id AS gid, MAX(p.count) AS peak_concurrent
            FROM playing_count_record p
            WHERE p.record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '7 days')) * 1000
              AND p.record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
            GROUP BY p.game_id
        ),
             avg_play AS (
                 SELECT a.game_id AS gid, AVG(a.count) AS avg_concurrent
                 FROM playing_count_record a
                 WHERE a.record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '7 days')) * 1000
                   AND a.record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
                 GROUP BY a.game_id
             ),
             growth AS (
                 SELECT t.gid AS gid,
                        CASE
                            WHEN l.avg_last > 0 THEN (t.avg_this - l.avg_last) / l.avg_last
                            ELSE 1
                            END AS growth_rate
                 FROM (
                          SELECT t1.game_id AS gid, AVG(t1.count) AS avg_this
                          FROM playing_count_record t1
                          WHERE t1.record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '7 days')) * 1000
                            AND t1.record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
                          GROUP BY t1.game_id
                      ) t
                          LEFT JOIN (
                     SELECT l1.game_id AS gid, AVG(l1.count) AS avg_last
                     FROM playing_count_record l1
                     WHERE l1.record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '14 days')) * 1000
                       AND l1.record_at < EXTRACT(EPOCH FROM (as_of_time - INTERVAL '7 days')) * 1000
                     GROUP BY l1.game_id
                 ) l ON t.gid = l.gid
             )
        SELECT p.gid AS game_id,
               p.peak_concurrent,
               a.avg_concurrent,
               g.growth_rate,
               (p.peak_concurrent * 0.4) + (a.avg_concurrent * 0.4) + (COALESCE(g.growth_rate,0) * 100 * 0.2) AS trending_score
        FROM peak p
                 JOIN avg_play a ON p.gid = a.gid
                 LEFT JOIN growth g ON p.gid = g.gid
                 JOIN game gm ON p.gid = gm.id
        WHERE gm.status = 0
        ORDER BY trending_score DESC
        LIMIT 50;
END;
$$;


CREATE OR REPLACE FUNCTION proc_monthly_trending(as_of_time TIMESTAMP with time zone DEFAULT NOW())
    RETURNS TABLE(
                     game_id BIGINT,
                     peak_concurrent BIGINT,
                     avg_concurrent NUMERIC(12,2),
                     growth_rate NUMERIC(6,2),
                     trending_score NUMERIC(12,2)
                 )
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        WITH peak AS (
            SELECT p.game_id AS gid, MAX(p.count) AS peak_concurrent
            FROM playing_count_record p
            WHERE p.record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '30 days')) * 1000
              AND p.record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
            GROUP BY p.game_id
        ),
             avg_play AS (
                 SELECT a.game_id AS gid, AVG(a.count) AS avg_concurrent
                 FROM playing_count_record a
                 WHERE a.record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '30 days')) * 1000
                   AND a.record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
                 GROUP BY a.game_id
             ),
             growth AS (
                 SELECT t.gid AS gid,
                        CASE
                            WHEN l.avg_last > 0 THEN (t.avg_this - l.avg_last) / l.avg_last
                            ELSE 1
                            END AS growth_rate
                 FROM (
                          SELECT t1.game_id AS gid, AVG(t1.count) AS avg_this
                          FROM playing_count_record t1
                          WHERE t1.record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '30 days')) * 1000
                            AND t1.record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
                          GROUP BY t1.game_id
                      ) t
                          LEFT JOIN (
                     SELECT l1.game_id AS gid, AVG(l1.count) AS avg_last
                     FROM playing_count_record l1
                     WHERE l1.record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '60 days')) * 1000
                       AND l1.record_at < EXTRACT(EPOCH FROM (as_of_time - INTERVAL '30 days')) * 1000
                     GROUP BY l1.game_id
                 ) l ON t.gid = l.gid
             )
        SELECT p.gid AS game_id,
               p.peak_concurrent,
               a.avg_concurrent,
               g.growth_rate,
               (p.peak_concurrent * 0.4) + (a.avg_concurrent * 0.4) + (COALESCE(g.growth_rate,0) * 100 * 0.2) AS trending_score
        FROM peak p
                 JOIN avg_play a ON p.gid = a.gid
                 LEFT JOIN growth g ON p.gid = g.gid
                 JOIN game gm ON p.gid = gm.id
        WHERE gm.status = 0
        ORDER BY trending_score DESC
        LIMIT 50;
END;
$$;



CREATE OR REPLACE FUNCTION proc_daily_trending(as_of_time TIMESTAMP with time zone DEFAULT NOW())
    RETURNS TABLE(
                     game_id BIGINT,
                     peak_concurrent BIGINT,
                     avg_concurrent NUMERIC(12,2),
                     growth_rate NUMERIC(6,2),
                     trending_score NUMERIC(12,2)
                 )
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
        WITH peak AS (
            SELECT p.game_id AS gid, MAX(p.count) AS peak_concurrent
            FROM playing_count_record p
            WHERE p.record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '1 days')) * 1000
              AND p.record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
            GROUP BY p.game_id
        ),
             avg_play AS (
                 SELECT a.game_id AS gid, AVG(a.count) AS avg_concurrent
                 FROM playing_count_record a
                 WHERE a.record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '1 days')) * 1000
                   AND a.record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
                 GROUP BY a.game_id
             ),
             growth AS (
                 SELECT t.gid AS gid,
                        CASE
                            WHEN l.avg_last > 0 THEN (t.avg_this - l.avg_last) / l.avg_last
                            ELSE 1
                            END AS growth_rate
                 FROM (
                          SELECT t1.game_id AS gid, AVG(t1.count) AS avg_this
                          FROM playing_count_record t1
                          WHERE t1.record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '1 days')) * 1000
                            AND t1.record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
                          GROUP BY t1.game_id
                      ) t
                          LEFT JOIN (
                     SELECT l1.game_id AS gid, AVG(l1.count) AS avg_last
                     FROM playing_count_record l1
                     WHERE l1.record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '2 days')) * 1000
                       AND l1.record_at < EXTRACT(EPOCH FROM (as_of_time - INTERVAL '1 days')) * 1000
                     GROUP BY l1.game_id
                 ) l ON t.gid = l.gid
             )
        SELECT p.gid AS game_id,
               p.peak_concurrent,
               a.avg_concurrent,
               g.growth_rate,
               (p.peak_concurrent * 0.4) + (a.avg_concurrent * 0.4) + (COALESCE(g.growth_rate,0) * 100 * 0.2) AS trending_score
        FROM peak p
                 JOIN avg_play a ON p.gid = a.gid
                 LEFT JOIN growth g ON p.gid = g.gid
                 JOIN game gm ON p.gid = gm.id
        WHERE gm.status = 0
        ORDER BY trending_score DESC
        LIMIT 50;
END;
$$;
