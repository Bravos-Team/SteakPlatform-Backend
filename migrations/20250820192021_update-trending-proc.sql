CREATE OR REPLACE FUNCTION proc_weekly_trending(as_of_time TIMESTAMP DEFAULT NOW())
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
            SELECT game_id, MAX(count) AS peak_concurrent
            FROM playing_count_record
            WHERE record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '7 days')) * 1000
              AND record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
            GROUP BY game_id
        ),
             avg_play AS (
                 SELECT game_id, AVG(count) AS avg_concurrent
                 FROM playing_count_record
                 WHERE record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '7 days')) * 1000
                   AND record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
                 GROUP BY game_id
             ),
             growth AS (
                 SELECT t.game_id,
                        CASE
                            WHEN l.avg_last > 0 THEN (t.avg_this - l.avg_last) / l.avg_last
                            ELSE 1
                            END AS growth_rate
                 FROM (
                          SELECT game_id, AVG(count) AS avg_this
                          FROM playing_count_record
                          WHERE record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '7 days')) * 1000
                            AND record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
                          GROUP BY game_id
                      ) t
                          LEFT JOIN (
                     SELECT game_id, AVG(count) AS avg_last
                     FROM playing_count_record
                     WHERE record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '14 days')) * 1000
                       AND record_at < EXTRACT(EPOCH FROM (as_of_time - INTERVAL '7 days')) * 1000
                     GROUP BY game_id
                 ) l ON t.game_id = l.game_id
             )
        SELECT p.game_id,
               p.peak_concurrent,
               a.avg_concurrent,
               g.growth_rate,
               (p.peak_concurrent * 0.4) + (a.avg_concurrent * 0.4) + (COALESCE(g.growth_rate,0) * 100 * 0.2) AS trending_score
        FROM peak p
                 JOIN avg_play a ON p.game_id = a.game_id
                 LEFT JOIN growth g ON p.game_id = g.game_id
                 JOIN game ON p.game_id = game.id
        WHERE game.status = 0
        ORDER BY trending_score DESC
        LIMIT 50;
END;
$$;


CREATE OR REPLACE FUNCTION proc_monthly_trending(as_of_time TIMESTAMP DEFAULT NOW())
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
            SELECT game_id, MAX(count) AS peak_concurrent
            FROM playing_count_record
            WHERE record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '30 days')) * 1000
              AND record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
            GROUP BY game_id
        ),
             avg_play AS (
                 SELECT game_id, AVG(count) AS avg_concurrent
                 FROM playing_count_record
                 WHERE record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '30 days')) * 1000
                   AND record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
                 GROUP BY game_id
             ),
             growth AS (
                 SELECT t.game_id,
                        CASE
                            WHEN l.avg_last > 0 THEN (t.avg_this - l.avg_last) / l.avg_last
                            ELSE 1
                            END AS growth_rate
                 FROM (
                          SELECT game_id, AVG(count) AS avg_this
                          FROM playing_count_record
                          WHERE record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '30 days')) * 1000
                            AND record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
                          GROUP BY game_id
                      ) t
                          LEFT JOIN (
                     SELECT game_id, AVG(count) AS avg_last
                     FROM playing_count_record
                     WHERE record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '60 days')) * 1000
                       AND record_at < EXTRACT(EPOCH FROM (as_of_time - INTERVAL '30 days')) * 1000
                     GROUP BY game_id
                 ) l ON t.game_id = l.game_id
             )
        SELECT p.game_id,
               p.peak_concurrent,
               a.avg_concurrent,
               g.growth_rate,
               (p.peak_concurrent * 0.4) + (a.avg_concurrent * 0.4) + (COALESCE(g.growth_rate,0) * 100 * 0.2) AS trending_score
        FROM peak p
                 JOIN avg_play a ON p.game_id = a.game_id
                 LEFT JOIN growth g ON p.game_id = g.game_id
                 JOIN game ON p.game_id = game.id
        WHERE game.status = 0
        ORDER BY trending_score DESC
        LIMIT 50;
END;
$$;


CREATE OR REPLACE FUNCTION proc_daily_trending(as_of_time TIMESTAMP DEFAULT NOW())
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
            SELECT game_id, MAX(count) AS peak_concurrent
            FROM playing_count_record
            WHERE record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '1 days')) * 1000
              AND record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
            GROUP BY game_id
        ),
             avg_play AS (
                 SELECT game_id, AVG(count) AS avg_concurrent
                 FROM playing_count_record
                 WHERE record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '1 days')) * 1000
                   AND record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
                 GROUP BY game_id
             ),
             growth AS (
                 SELECT t.game_id,
                        CASE
                            WHEN l.avg_last > 0 THEN (t.avg_this - l.avg_last) / l.avg_last
                            ELSE 1
                            END AS growth_rate
                 FROM (
                          SELECT game_id, AVG(count) AS avg_this
                          FROM playing_count_record
                          WHERE record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '1 days')) * 1000
                            AND record_at < EXTRACT(EPOCH FROM as_of_time) * 1000
                          GROUP BY game_id
                      ) t
                          LEFT JOIN (
                     SELECT game_id, AVG(count) AS avg_last
                     FROM playing_count_record
                     WHERE record_at >= EXTRACT(EPOCH FROM (as_of_time - INTERVAL '2 days')) * 1000
                       AND record_at < EXTRACT(EPOCH FROM (as_of_time - INTERVAL '1 days')) * 1000
                     GROUP BY game_id
                 ) l ON t.game_id = l.game_id
             )
        SELECT p.game_id,
               p.peak_concurrent,
               a.avg_concurrent,
               g.growth_rate,
               (p.peak_concurrent * 0.4) + (a.avg_concurrent * 0.4) + (COALESCE(g.growth_rate,0) * 100 * 0.2) AS trending_score
        FROM peak p
                 JOIN avg_play a ON p.game_id = a.game_id
                 LEFT JOIN growth g ON p.game_id = g.game_id
                 JOIN game ON p.game_id = game.id
        WHERE game.status = 0
        ORDER BY trending_score DESC
        LIMIT 50;
END;
$$;
