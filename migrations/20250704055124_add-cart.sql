-- Create "cart" table
CREATE TABLE "public"."cart" (
  "id" bigint NOT NULL,
  "user_account_id" bigint NULL,
  "created_at" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "updated_at" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY ("id"),
  CONSTRAINT "cart_user_account_id_fkey" FOREIGN KEY ("user_account_id") REFERENCES "public"."user_account" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_cart_user_account_id" to table: "cart"
CREATE UNIQUE INDEX "idx_cart_user_account_id" ON "public"."cart" ("user_account_id");
-- Create "cart_item" table
CREATE TABLE "public"."cart_item" (
  "id" bigint NOT NULL,
  "cart_id" bigint NOT NULL,
  "game_id" bigint NOT NULL,
  "added_at" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY ("id"),
  CONSTRAINT "cart_item_cart_id_fkey" FOREIGN KEY ("cart_id") REFERENCES "public"."cart" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "cart_item_game_id_fkey" FOREIGN KEY ("game_id") REFERENCES "public"."game" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_cart_item_cart_id" to table: "cart_item"
CREATE INDEX "idx_cart_item_cart_id" ON "public"."cart_item" ("cart_id");
-- Create index "unque_cart_item_game_id" to table: "cart_item"
CREATE UNIQUE INDEX "unque_cart_item_game_id" ON "public"."cart_item" ("cart_id", "game_id");
