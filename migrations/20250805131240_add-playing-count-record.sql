-- Create "playing_count_record" table
CREATE TABLE "public"."playing_count_record" (
  "id" bigint NOT NULL,
  "game_id" bigint NOT NULL,
  "count" bigint NOT NULL DEFAULT 0,
  "record_from" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  "record_to" bigint NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "playing_count_record_game_id_fkey" FOREIGN KEY ("game_id") REFERENCES "public"."game" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_playing_count_record_game_id" to table: "playing_count_record"
CREATE INDEX "idx_playing_count_record_game_id" ON "public"."playing_count_record" ("game_id");
-- Create index "idx_playing_count_record_record_from" to table: "playing_count_record"
CREATE INDEX "idx_playing_count_record_record_from" ON "public"."playing_count_record" ("record_from", "record_to");
-- Create index "idx_playing_count_record_record_to" to table: "playing_count_record"
CREATE INDEX "idx_playing_count_record_record_to" ON "public"."playing_count_record" ("record_to");
