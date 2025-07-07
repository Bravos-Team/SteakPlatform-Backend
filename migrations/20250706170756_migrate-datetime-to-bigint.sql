-- Set comment to schema: "public"
COMMENT ON SCHEMA "public" IS 'Main schema';
-- Create "admin_account" table
CREATE TABLE "public"."admin_account" (
  "id" bigint NOT NULL,
  "username" character varying(32) NOT NULL,
  "password" character varying(255) NOT NULL,
  "email" character varying(255) NOT NULL,
  "status" smallint NOT NULL DEFAULT 0,
  "enable_mfa" boolean NOT NULL DEFAULT false,
  "mfa_secret" character varying(255) NULL,
  "created_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  "updated_at" bigint NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  PRIMARY KEY ("id"),
  CONSTRAINT "admin_account_email_key" UNIQUE ("email"),
  CONSTRAINT "admin_account_username_key" UNIQUE ("username")
);
-- Create index "idx_admin_email" to table: "admin_account"
CREATE INDEX "idx_admin_email" ON "public"."admin_account" ("username");
-- Create index "idx_admin_username" to table: "admin_account"
CREATE INDEX "idx_admin_username" ON "public"."admin_account" ("username");
-- Create "admin_role" table
CREATE TABLE "public"."admin_role" (
  "id" bigint NOT NULL,
  "name" character varying(255) NOT NULL,
  "active" boolean NOT NULL DEFAULT true,
  "description" character varying(255) NULL,
  "updated_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  PRIMARY KEY ("id")
);
-- Create "admin_account_role" table
CREATE TABLE "public"."admin_account_role" (
  "admin_account_id" bigint NOT NULL,
  "admin_role_id" bigint NOT NULL,
  PRIMARY KEY ("admin_account_id", "admin_role_id"),
  CONSTRAINT "admin_account_role_admin_account_id_fkey" FOREIGN KEY ("admin_account_id") REFERENCES "public"."admin_account" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "admin_account_role_admin_role_id_fkey" FOREIGN KEY ("admin_role_id") REFERENCES "public"."admin_role" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_admin_account_role_account_id" to table: "admin_account_role"
CREATE INDEX "idx_admin_account_role_account_id" ON "public"."admin_account_role" ("admin_account_id");
-- Create "admin_permission_group" table
CREATE TABLE "public"."admin_permission_group" (
  "id" integer NOT NULL GENERATED ALWAYS AS IDENTITY,
  "name" character varying(255) NOT NULL,
  "description" character varying(255) NULL,
  PRIMARY KEY ("id")
);
-- Create "admin_permission" table
CREATE TABLE "public"."admin_permission" (
  "id" integer NOT NULL GENERATED ALWAYS AS IDENTITY,
  "group_id" integer NOT NULL,
  "name" character varying(64) NOT NULL,
  "description" character varying(255) NULL,
  "authorities" jsonb NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "admin_permission_name_key" UNIQUE ("name"),
  CONSTRAINT "admin_permission_group_id_fkey" FOREIGN KEY ("group_id") REFERENCES "public"."admin_permission_group" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create "admin_permission_role" table
CREATE TABLE "public"."admin_permission_role" (
  "admin_role_id" bigint NOT NULL,
  "admin_permission_id" integer NOT NULL,
  PRIMARY KEY ("admin_role_id", "admin_permission_id"),
  CONSTRAINT "admin_permission_role_admin_permission_id_fkey" FOREIGN KEY ("admin_permission_id") REFERENCES "public"."admin_permission" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "admin_permission_role_admin_role_id_fkey" FOREIGN KEY ("admin_role_id") REFERENCES "public"."admin_role" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_admin_permission_id" to table: "admin_permission_role"
CREATE INDEX "idx_admin_permission_id" ON "public"."admin_permission_role" ("admin_permission_id");
-- Create index "idx_admin_role_id" to table: "admin_permission_role"
CREATE INDEX "idx_admin_role_id" ON "public"."admin_permission_role" ("admin_role_id");
-- Create "admin_refresh_token" table
CREATE TABLE "public"."admin_refresh_token" (
  "id" bigint NOT NULL,
  "account_id" bigint NOT NULL,
  "device_id" character varying(64) NOT NULL,
  "issues_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  "expires_at" bigint NOT NULL,
  "revoked" boolean NOT NULL DEFAULT false,
  "token" character varying(64) NOT NULL,
  "device_info" character varying(255) NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "admin_refresh_token_token_key" UNIQUE ("token"),
  CONSTRAINT "admin_refresh_token_account_id_fkey" FOREIGN KEY ("account_id") REFERENCES "public"."admin_account" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_admin_account_id" to table: "admin_refresh_token"
CREATE INDEX "idx_admin_account_id" ON "public"."admin_refresh_token" ("account_id");
-- Create index "idx_admin_token_device_id" to table: "admin_refresh_token"
CREATE INDEX "idx_admin_token_device_id" ON "public"."admin_refresh_token" ("token", "device_id");
-- Create "user_account" table
CREATE TABLE "public"."user_account" (
  "id" bigint NOT NULL,
  "username" character varying(32) NOT NULL,
  "password" character varying(255) NOT NULL,
  "email" character varying(255) NOT NULL,
  "status" smallint NOT NULL DEFAULT 0,
  "enable_mfa" boolean NOT NULL DEFAULT false,
  "mfa_secret" character varying(255) NULL,
  "created_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  "updated_at" bigint NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  PRIMARY KEY ("id"),
  CONSTRAINT "user_account_email_key" UNIQUE ("email"),
  CONSTRAINT "user_account_username_key" UNIQUE ("username")
);
-- Create index "idx_email" to table: "user_account"
CREATE INDEX "idx_email" ON "public"."user_account" ("username");
-- Create index "idx_username" to table: "user_account"
CREATE INDEX "idx_username" ON "public"."user_account" ("username");
-- Create "cart" table
CREATE TABLE "public"."cart" (
  "id" bigint NOT NULL,
  "user_account_id" bigint NULL,
  "created_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  "updated_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  PRIMARY KEY ("id"),
  CONSTRAINT "cart_user_account_id_fkey" FOREIGN KEY ("user_account_id") REFERENCES "public"."user_account" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_cart_user_account_id" to table: "cart"
CREATE UNIQUE INDEX "idx_cart_user_account_id" ON "public"."cart" ("user_account_id");
-- Create "publisher" table
CREATE TABLE "public"."publisher" (
  "id" bigint NOT NULL,
  "name" character varying(255) NOT NULL,
  "email" character varying(255) NOT NULL,
  "phone" character varying(15) NOT NULL,
  "status" smallint NOT NULL,
  "logo_url" character varying(255) NULL,
  "created_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  "updated_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  PRIMARY KEY ("id"),
  CONSTRAINT "publisher_email_key" UNIQUE ("email"),
  CONSTRAINT "publisher_name_key" UNIQUE ("name")
);
-- Create "game" table
CREATE TABLE "public"."game" (
  "id" bigint NOT NULL,
  "publisher_id" bigint NOT NULL,
  "name" character varying(255) NOT NULL,
  "price" numeric(13,2) NOT NULL,
  "status" integer NOT NULL,
  "release_date" bigint NOT NULL,
  "created_at" bigint NOT NULL,
  "updated_at" bigint NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "game_publisher_id_fkey" FOREIGN KEY ("publisher_id") REFERENCES "public"."publisher" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_game_name" to table: "game"
CREATE INDEX "idx_game_name" ON "public"."game" ("name");
-- Create index "idx_game_publisher" to table: "game"
CREATE INDEX "idx_game_publisher" ON "public"."game" ("publisher_id");
-- Create index "idx_game_release_date" to table: "game"
CREATE INDEX "idx_game_release_date" ON "public"."game" ("release_date");
-- Create "cart_item" table
CREATE TABLE "public"."cart_item" (
  "id" bigint NOT NULL,
  "cart_id" bigint NOT NULL,
  "game_id" bigint NOT NULL,
  "added_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  PRIMARY KEY ("id"),
  CONSTRAINT "cart_item_cart_id_fkey" FOREIGN KEY ("cart_id") REFERENCES "public"."cart" ("id") ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT "cart_item_game_id_fkey" FOREIGN KEY ("game_id") REFERENCES "public"."game" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_cart_item_cart_id" to table: "cart_item"
CREATE INDEX "idx_cart_item_cart_id" ON "public"."cart_item" ("cart_id");
-- Create index "unque_cart_item_game_id" to table: "cart_item"
CREATE UNIQUE INDEX "unque_cart_item_game_id" ON "public"."cart_item" ("cart_id", "game_id");
-- Create "genre" table
CREATE TABLE "public"."genre" (
  "id" integer NOT NULL GENERATED ALWAYS AS IDENTITY,
  "name" character varying(64) NOT NULL,
  "description" text NULL,
  "slug" character varying(128) NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "genre_slug_key" UNIQUE ("slug")
);
-- Create index "idx_genre_slug" to table: "genre"
CREATE INDEX "idx_genre_slug" ON "public"."genre" ("slug");
-- Create "game_genre" table
CREATE TABLE "public"."game_genre" (
  "game_id" bigint NOT NULL,
  "genre_id" bigint NOT NULL,
  PRIMARY KEY ("game_id", "genre_id"),
  CONSTRAINT "game_genre_game_id_fkey" FOREIGN KEY ("game_id") REFERENCES "public"."game" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "game_genre_genre_id_fkey" FOREIGN KEY ("genre_id") REFERENCES "public"."genre" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_genre_game" to table: "game_genre"
CREATE INDEX "idx_genre_game" ON "public"."game_genre" ("game_id");
-- Create index "idx_genre_genre" to table: "game_genre"
CREATE INDEX "idx_genre_genre" ON "public"."game_genre" ("genre_id");
-- Create "tag" table
CREATE TABLE "public"."tag" (
  "id" integer NOT NULL GENERATED ALWAYS AS IDENTITY,
  "name" character varying(64) NOT NULL,
  "description" text NULL,
  "slug" character varying(128) NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "tag_slug_key" UNIQUE ("slug")
);
-- Create index "idx_tag_slug" to table: "tag"
CREATE INDEX "idx_tag_slug" ON "public"."tag" ("slug");
-- Create "game_tag" table
CREATE TABLE "public"."game_tag" (
  "game_id" bigint NOT NULL,
  "tag_id" bigint NOT NULL,
  PRIMARY KEY ("game_id", "tag_id"),
  CONSTRAINT "game_tag_game_id_fkey" FOREIGN KEY ("game_id") REFERENCES "public"."game" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "game_tag_tag_id_fkey" FOREIGN KEY ("tag_id") REFERENCES "public"."tag" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_game_tag_game_id" to table: "game_tag"
CREATE INDEX "idx_game_tag_game_id" ON "public"."game_tag" ("game_id");
-- Create index "idx_game_tag_tag_id" to table: "game_tag"
CREATE INDEX "idx_game_tag_tag_id" ON "public"."game_tag" ("tag_id");
-- Create "game_version" table
CREATE TABLE "public"."game_version" (
  "id" bigint NOT NULL,
  "game_id" bigint NOT NULL,
  "name" character varying(255) NOT NULL,
  "change_log" text NULL,
  "exec_path" character varying(255) NOT NULL,
  "download_url" character varying(255) NOT NULL,
  "status" integer NOT NULL,
  "release_date" bigint NOT NULL,
  "created_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  "updated_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  PRIMARY KEY ("id"),
  CONSTRAINT "game_version_game_id_fkey" FOREIGN KEY ("game_id") REFERENCES "public"."game" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_release_date" to table: "game_version"
CREATE INDEX "idx_release_date" ON "public"."game_version" ("game_id", "release_date", "status");
-- Create index "idx_version_game" to table: "game_version"
CREATE INDEX "idx_version_game" ON "public"."game_version" ("game_id");
-- Create "orders" table
CREATE TABLE "public"."orders" (
  "id" bigint NOT NULL,
  "user_account_id" bigint NOT NULL,
  "status" smallint NOT NULL,
  "message" character varying(255) NULL,
  "created_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  "updated_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  PRIMARY KEY ("id"),
  CONSTRAINT "orders_user_account_id_fkey" FOREIGN KEY ("user_account_id") REFERENCES "public"."user_account" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_order_user_account_id" to table: "orders"
CREATE INDEX "idx_order_user_account_id" ON "public"."orders" ("user_account_id");
-- Create "order_details" table
CREATE TABLE "public"."order_details" (
  "id" bigint NOT NULL,
  "order_id" bigint NOT NULL,
  "game_id" bigint NOT NULL,
  "price" numeric(13,2) NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "order_details_game_id_fkey" FOREIGN KEY ("game_id") REFERENCES "public"."game" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "order_details_order_id_fkey" FOREIGN KEY ("order_id") REFERENCES "public"."orders" ("id") ON UPDATE NO ACTION ON DELETE CASCADE
);
-- Create index "idx_order_details_order_id" to table: "order_details"
CREATE INDEX "idx_order_details_order_id" ON "public"."order_details" ("order_id");
-- Create "publisher_account" table
CREATE TABLE "public"."publisher_account" (
  "id" bigint NOT NULL,
  "publisher_id" bigint NOT NULL,
  "username" character varying(32) NOT NULL,
  "password" character varying(255) NOT NULL,
  "email" character varying(255) NOT NULL,
  "status" smallint NOT NULL DEFAULT 0,
  "enable_mfa" boolean NOT NULL DEFAULT false,
  "mfa_secret" character varying(255) NULL,
  "created_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  "updated_at" bigint NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  PRIMARY KEY ("id"),
  CONSTRAINT "publisher_account_email_key" UNIQUE ("email"),
  CONSTRAINT "publisher_account_username_key" UNIQUE ("username"),
  CONSTRAINT "publisher_account_publisher_id_fkey" FOREIGN KEY ("publisher_id") REFERENCES "public"."publisher" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_account_publisher" to table: "publisher_account"
CREATE INDEX "idx_account_publisher" ON "public"."publisher_account" ("publisher_id");
-- Create index "idx_publisher_email" to table: "publisher_account"
CREATE INDEX "idx_publisher_email" ON "public"."publisher_account" ("email");
-- Create index "idx_publisher_username" to table: "publisher_account"
CREATE INDEX "idx_publisher_username" ON "public"."publisher_account" ("username");
-- Create "publisher_role" table
CREATE TABLE "public"."publisher_role" (
  "id" bigint NOT NULL,
  "publisher_id" bigint NULL,
  "name" character varying(255) NOT NULL,
  "active" boolean NOT NULL DEFAULT true,
  "description" character varying(255) NULL,
  "updated_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  PRIMARY KEY ("id"),
  CONSTRAINT "publisher_role_publisher_id_fkey" FOREIGN KEY ("publisher_id") REFERENCES "public"."publisher" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_publisher_role_publisher_id" to table: "publisher_role"
CREATE INDEX "idx_publisher_role_publisher_id" ON "public"."publisher_role" ("publisher_id");
-- Create "publisher_account_role" table
CREATE TABLE "public"."publisher_account_role" (
  "publisher_account_id" bigint NOT NULL,
  "publisher_role_id" bigint NOT NULL,
  PRIMARY KEY ("publisher_account_id", "publisher_role_id"),
  CONSTRAINT "publisher_account_role_publisher_account_id_fkey" FOREIGN KEY ("publisher_account_id") REFERENCES "public"."publisher_account" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "publisher_account_role_publisher_role_id_fkey" FOREIGN KEY ("publisher_role_id") REFERENCES "public"."publisher_role" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_publisher_account_role_account_id" to table: "publisher_account_role"
CREATE INDEX "idx_publisher_account_role_account_id" ON "public"."publisher_account_role" ("publisher_account_id");
-- Create index "idx_publisher_account_role_both" to table: "publisher_account_role"
CREATE INDEX "idx_publisher_account_role_both" ON "public"."publisher_account_role" ("publisher_account_id", "publisher_role_id");
-- Create "publisher_permission_group" table
CREATE TABLE "public"."publisher_permission_group" (
  "id" integer NOT NULL GENERATED ALWAYS AS IDENTITY,
  "name" character varying(255) NOT NULL,
  "description" character varying(255) NULL,
  PRIMARY KEY ("id")
);
-- Create "publisher_permission" table
CREATE TABLE "public"."publisher_permission" (
  "id" integer NOT NULL GENERATED ALWAYS AS IDENTITY,
  "group_id" integer NOT NULL,
  "name" character varying(64) NOT NULL,
  "description" character varying(255) NULL,
  "authorities" jsonb NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "publisher_permission_name_key" UNIQUE ("name"),
  CONSTRAINT "publisher_permission_group_id_fkey" FOREIGN KEY ("group_id") REFERENCES "public"."publisher_permission_group" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create "publisher_permission_role" table
CREATE TABLE "public"."publisher_permission_role" (
  "publisher_role_id" bigint NOT NULL,
  "publisher_permission_id" integer NOT NULL,
  PRIMARY KEY ("publisher_role_id", "publisher_permission_id"),
  CONSTRAINT "publisher_permission_role_publisher_permission_id_fkey" FOREIGN KEY ("publisher_permission_id") REFERENCES "public"."publisher_permission" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "publisher_permission_role_publisher_role_id_fkey" FOREIGN KEY ("publisher_role_id") REFERENCES "public"."publisher_role" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_permission_id" to table: "publisher_permission_role"
CREATE INDEX "idx_permission_id" ON "public"."publisher_permission_role" ("publisher_permission_id");
-- Create index "idx_role_id" to table: "publisher_permission_role"
CREATE INDEX "idx_role_id" ON "public"."publisher_permission_role" ("publisher_role_id");
-- Create "publisher_refresh_token" table
CREATE TABLE "public"."publisher_refresh_token" (
  "id" bigint NOT NULL,
  "account_id" bigint NOT NULL,
  "device_id" character varying(64) NOT NULL,
  "issues_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  "expires_at" bigint NOT NULL,
  "revoked" boolean NOT NULL DEFAULT false,
  "token" character varying(64) NOT NULL,
  "device_info" character varying(255) NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "publisher_refresh_token_token_key" UNIQUE ("token"),
  CONSTRAINT "publisher_refresh_token_account_id_fkey" FOREIGN KEY ("account_id") REFERENCES "public"."publisher_account" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_publisher_account_id" to table: "publisher_refresh_token"
CREATE INDEX "idx_publisher_account_id" ON "public"."publisher_refresh_token" ("account_id");
-- Create index "idx_publisher_token_device_id" to table: "publisher_refresh_token"
CREATE INDEX "idx_publisher_token_device_id" ON "public"."publisher_refresh_token" ("token", "device_id");
-- Create "user_game" table
CREATE TABLE "public"."user_game" (
  "user_account_id" bigint NOT NULL,
  "game_id" bigint NOT NULL,
  "owned_date" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  "play_seconds" bigint NOT NULL DEFAULT 0,
  "play_recent_date" bigint NULL,
  PRIMARY KEY ("user_account_id", "game_id"),
  CONSTRAINT "user_game_game_id_fkey" FOREIGN KEY ("game_id") REFERENCES "public"."game" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "user_game_user_account_id_fkey" FOREIGN KEY ("user_account_id") REFERENCES "public"."user_account" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_user_game_game_id" to table: "user_game"
CREATE INDEX "idx_user_game_game_id" ON "public"."user_game" ("game_id");
-- Create index "idx_user_game_user_account_id" to table: "user_game"
CREATE INDEX "idx_user_game_user_account_id" ON "public"."user_game" ("user_account_id");
-- Create "user_oauth2_account" table
CREATE TABLE "public"."user_oauth2_account" (
  "id" bigint NOT NULL,
  "oauth2_id" character varying(255) NOT NULL,
  "oauth2_provider" character varying(255) NOT NULL,
  "status" smallint NOT NULL,
  "user_account_id" bigint NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "user_oauth2_account_user_account_id_fkey" FOREIGN KEY ("user_account_id") REFERENCES "public"."user_account" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_oauth2_id_provider" to table: "user_oauth2_account"
CREATE INDEX "idx_oauth2_id_provider" ON "public"."user_oauth2_account" ("oauth2_id", "oauth2_provider");
-- Create index "idx_user_account_id" to table: "user_oauth2_account"
CREATE INDEX "idx_user_account_id" ON "public"."user_oauth2_account" ("user_account_id");
-- Create "user_refresh_token" table
CREATE TABLE "public"."user_refresh_token" (
  "id" bigint NOT NULL,
  "account_id" bigint NOT NULL,
  "device_id" character varying(64) NOT NULL,
  "issues_at" bigint NOT NULL DEFAULT ((EXTRACT(epoch FROM (CURRENT_TIMESTAMP AT TIME ZONE 'Asia/Ho_Chi_Minh'::text)) * (1000)::numeric))::bigint,
  "expires_at" bigint NOT NULL,
  "revoked" boolean NOT NULL DEFAULT false,
  "token" character varying(64) NOT NULL,
  "device_info" character varying(255) NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "user_refresh_token_token_key" UNIQUE ("token"),
  CONSTRAINT "user_refresh_token_account_id_fkey" FOREIGN KEY ("account_id") REFERENCES "public"."user_account" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_account_id" to table: "user_refresh_token"
CREATE INDEX "idx_account_id" ON "public"."user_refresh_token" ("account_id");
-- Create index "idx_token_device_id" to table: "user_refresh_token"
CREATE INDEX "idx_token_device_id" ON "public"."user_refresh_token" ("token", "device_id");
