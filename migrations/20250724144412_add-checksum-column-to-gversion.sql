-- Modify "game_version" table
ALTER TABLE "public"."game_version" ADD COLUMN "checksum" character varying(64) NULL;
