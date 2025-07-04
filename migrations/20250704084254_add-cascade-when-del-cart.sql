-- Modify "cart_item" table
ALTER TABLE "public"."cart_item" DROP CONSTRAINT "cart_item_cart_id_fkey", ADD CONSTRAINT "cart_item_cart_id_fkey" FOREIGN KEY ("cart_id") REFERENCES "public"."cart" ("id") ON UPDATE NO ACTION ON DELETE CASCADE;
