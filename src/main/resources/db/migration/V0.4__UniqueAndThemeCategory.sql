ALTER TABLE "public"."vocabulary"
    ADD UNIQUE ("word");
ALTER TABLE "public"."sentence"
    ADD UNIQUE ("sentence");

CREATE TABLE category
(
    id    integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title text UNIQUE
);

INSERT INTO "public"."category"("title")
VALUES ('Film')
RETURNING "id", "title";
INSERT INTO "public"."category"("title")
VALUES ('Book')
RETURNING "id", "title";
INSERT INTO "public"."category"("title")
VALUES ('Article')
RETURNING "id", "title";

ALTER TABLE "public"."theme"
    ADD COLUMN "category_id" integer DEFAULT 1 NOT NULL,
    ADD FOREIGN KEY ("category_id") REFERENCES "public"."category" ("id");
