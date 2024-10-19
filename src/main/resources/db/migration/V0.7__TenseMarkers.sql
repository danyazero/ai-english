ALTER TABLE "public"."sentence"
    DROP COLUMN "grammar_task";

ALTER TABLE "public"."vocabulary_sentence"
    ADD COLUMN "order"         smallint NOT NULL DEFAULT '0',
    ADD COLUMN "sentence_form" text     NOT NULL DEFAULT '""',
    ADD COLUMN "is_marker"     boolean  NOT NULL DEFAULT FALSE;

UPDATE "public"."vocabulary_sentence" SET "order"=3, "sentence_form"='with' WHERE "id"=4 RETURNING "id", "sentence_id", "vocabulary_id", "order", "sentence_form", "is_marker";
UPDATE "public"."vocabulary_sentence" SET "order"=2, "sentence_form"='cope', "is_marker"=TRUE WHERE "id"=3 RETURNING "id", "sentence_id", "vocabulary_id", "order", "sentence_form", "is_marker";
UPDATE "public"."vocabulary_sentence" SET "order"=1, "sentence_form"='will', "is_marker"=TRUE WHERE "id"=2 RETURNING "id", "sentence_id", "vocabulary_id", "order", "sentence_form", "is_marker";
UPDATE "public"."vocabulary_sentence" SET "order"=2, "sentence_form"='acing', "is_marker"=TRUE WHERE "id"=10 RETURNING "id", "sentence_id", "vocabulary_id", "order", "sentence_form", "is_marker";
UPDATE "public"."vocabulary_sentence" SET "sentence_form"='I' WHERE "id"=1 OR "id"=8 RETURNING "id", "sentence_id", "vocabulary_id", "order", "sentence_form", "is_marker";
UPDATE "public"."vocabulary_sentence" SET "order"=1, "sentence_form"='am', "is_marker"=TRUE WHERE "id"=9 RETURNING "id", "sentence_id", "vocabulary_id", "order", "sentence_form", "is_marker";
UPDATE "public"."vocabulary_sentence" SET "order"=3, "sentence_form"='it' WHERE "id"=11 RETURNING "id", "sentence_id", "vocabulary_id", "order", "sentence_form", "is_marker";
UPDATE "public"."vocabulary_sentence" SET "order"=4, "sentence_form"='it' WHERE "id"=5 RETURNING "id", "sentence_id", "vocabulary_id", "order", "sentence_form", "is_marker";
UPDATE "public"."vocabulary_sentence" SET "order"=4, "sentence_form"='by' WHERE "id"=12 RETURNING "id", "sentence_id", "vocabulary_id", "order", "sentence_form", "is_marker";
UPDATE "public"."vocabulary_sentence" SET "order"=5, "sentence_form"='next' WHERE "id"=6 OR "id"=13 RETURNING "id", "sentence_id", "vocabulary_id", "order", "sentence_form", "is_marker";
UPDATE "public"."vocabulary_sentence" SET "order"=6, "sentence_form"='Friday' WHERE "id"=7 OR "id"=14 RETURNING "id", "sentence_id", "vocabulary_id", "order", "sentence_form", "is_marker";

UPDATE "public"."speech_part" SET "title"='Unknown', "translate"='Невідомо', "answers_to"='__' WHERE "id"=1 RETURNING "id", "title", "translate", "answers_to";

INSERT INTO "public"."speech_part"("title", "translate", "answers_to") VALUES('Determiner', 'Визначник', '--') RETURNING "id", "title", "translate", "answers_to";
INSERT INTO "public"."speech_part"("title", "translate", "answers_to") VALUES('Gerund', 'Герундій', '--') RETURNING "id", "title", "translate", "answers_to";
INSERT INTO "public"."speech_part"("title", "translate", "answers_to") VALUES('Infinitive', 'Інфінітив', '--') RETURNING "id", "title", "translate", "answers_to";
INSERT INTO "public"."speech_part"("title", "translate", "answers_to") VALUES('Participle', 'Дієприкметник', 'Який? Яка?') RETURNING "id", "title", "translate", "answers_to";
INSERT INTO "public"."speech_part"("title", "translate", "answers_to") VALUES('Pronoun', 'Займенник', 'Хто? Що?') RETURNING "id", "title", "translate", "answers_to";

UPDATE "public"."vocabulary" SET "speech_part_id"=15 WHERE "id"=1 OR "id"=5 RETURNING "id", "word", "translate", "views", "speech_part_id";
