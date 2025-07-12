-- Drop index "idx_wishlist_user_account_id" from table: "wishlist"
DROP INDEX "public"."idx_wishlist_user_account_id";
-- Create index "idx_wishlist_user_account_id" to table: "wishlist"
CREATE INDEX "idx_wishlist_user_account_id" ON "public"."wishlist" ("user_account_id");
-- Create index "idx_unique_wishlist" to table: "wishlist"
CREATE UNIQUE INDEX "idx_unique_wishlist" ON "public"."wishlist" ("user_account_id", "game_id");
