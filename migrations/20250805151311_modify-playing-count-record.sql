-- Rename a column from "record_from" to "record_at"
ALTER TABLE "public"."playing_count_record" RENAME COLUMN "record_from" TO "record_at";
-- Modify "playing_count_record" table
ALTER TABLE "public"."playing_count_record" DROP COLUMN "record_to";
-- Create index "idx_playing_count_record_record_at" to table: "playing_count_record"
CREATE INDEX "idx_playing_count_record_record_at" ON "public"."playing_count_record" ("game_id", "record_at");
