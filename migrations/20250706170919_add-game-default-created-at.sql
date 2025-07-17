-- Modify "game" table
ALTER TABLE "public"."game" ALTER COLUMN "created_at" SET DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint;
