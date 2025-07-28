-- Modify "game_version" table
ALTER TABLE "public"."game_version" ADD CONSTRAINT "version_game_name_key" UNIQUE ("game_id", "name");
