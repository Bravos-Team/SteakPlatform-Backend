-- Create "wishlist" table
CREATE TABLE "public"."wishlist" (
  "id" bigint NOT NULL,
  "user_account_id" bigint NOT NULL,
  "game_id" bigint NOT NULL,
  "added_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  PRIMARY KEY ("id"),
  CONSTRAINT "wishlist_game_id_fkey" FOREIGN KEY ("game_id") REFERENCES "public"."game" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "wishlist_user_account_id_fkey" FOREIGN KEY ("user_account_id") REFERENCES "public"."user_account" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_wishlist_user_account_id" to table: "wishlist"
CREATE UNIQUE INDEX "idx_wishlist_user_account_id" ON "public"."wishlist" ("user_account_id");
