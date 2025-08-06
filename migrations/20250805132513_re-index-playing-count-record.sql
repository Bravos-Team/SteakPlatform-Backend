-- Drop index "idx_playing_count_record_record_from" from table: "playing_count_record"
DROP INDEX "public"."idx_playing_count_record_record_from";
-- Drop index "idx_playing_count_record_record_to" from table: "playing_count_record"
DROP INDEX "public"."idx_playing_count_record_record_to";
-- Create index "idx_playing_count_record_record_from" to table: "playing_count_record"
CREATE INDEX "idx_playing_count_record_record_from" ON "public"."playing_count_record" ("game_id", "record_from", "record_to");
-- Create index "idx_playing_count_record_record_to" to table: "playing_count_record"
CREATE INDEX "idx_playing_count_record_record_to" ON "public"."playing_count_record" ("game_id", "record_to");
