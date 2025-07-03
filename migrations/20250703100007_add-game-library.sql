-- Create "user_game" table
CREATE TABLE "public"."user_game" (
  "user_account_id" bigint NOT NULL,
  "game_id" bigint NOT NULL,
  "owned_date" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "play_seconds" bigint NOT NULL DEFAULT 0,
  "play_recent_date" timestamp NULL,
  PRIMARY KEY ("user_account_id", "game_id"),
  CONSTRAINT "user_game_game_id_fkey" FOREIGN KEY ("game_id") REFERENCES "public"."game" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "user_game_user_account_id_fkey" FOREIGN KEY ("user_account_id") REFERENCES "public"."user_account" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_user_game_game_id" to table: "user_game"
CREATE INDEX "idx_user_game_game_id" ON "public"."user_game" ("game_id");
-- Create index "idx_user_game_user_account_id" to table: "user_game"
CREATE INDEX "idx_user_game_user_account_id" ON "public"."user_game" ("user_account_id");
