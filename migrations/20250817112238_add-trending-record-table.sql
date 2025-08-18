-- Create "top_50_monthly_trending" table
CREATE TABLE "public"."top_50_monthly_trending" (
  "year" integer NOT NULL,
  "month" integer NOT NULL,
  "rank" integer NOT NULL,
  "game_id" bigint NOT NULL,
  "peak_concurrent" bigint NOT NULL,
  "avg_concurrent" numeric(12,2) NOT NULL,
  "growth_rate" numeric(6,2) NOT NULL,
  "trending_score" numeric(12,2) NOT NULL,
  PRIMARY KEY ("year", "month", "rank"),
  CONSTRAINT "top_50_monthly_trending_game_id_fkey" FOREIGN KEY ("game_id") REFERENCES "public"."game" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION
);
-- Create index "idx_top_50_monthly_trending_game_id" to table: "top_50_monthly_trending"
CREATE INDEX "idx_top_50_monthly_trending_game_id" ON "public"."top_50_monthly_trending" ("game_id");
-- Create index "idx_top_50_monthly_trending_year_month" to table: "top_50_monthly_trending"
CREATE INDEX "idx_top_50_monthly_trending_year_month" ON "public"."top_50_monthly_trending" ("year", "month");
