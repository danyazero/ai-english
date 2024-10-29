ALTER TABLE "public"."vocabulary"
    DROP CONSTRAINT "vocabulary_word_key";
ALTER TABLE "public"."theme"
    ADD COLUMN "last_update" timestamp NOT NULL DEFAULT now();

UPDATE "public"."theme"
SET "last_update"='2024-10-24 14:01:59.088957'
WHERE "id" = 2
   OR "id" = 3
   OR "id" = 9
   OR "id" = 11
   OR "id" = 7
RETURNING "id", "title", "year", "category_id", "last_update";

ALTER TABLE "public"."vocabulary_sentence" RENAME COLUMN "sentence_form" TO "default_word";
