ALTER TABLE "public"."vocabulary_sentence"
    ALTER COLUMN "vocabulary_id" DROP NOT NULL;

CREATE TABLE status
(
    id    integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title text NOT NULL
);

INSERT INTO "public"."status"("title")
VALUES ('ASKED')
RETURNING "id", "title";
INSERT INTO "public"."status"("title")
VALUES ('CORRECT')
RETURNING "id", "title";
INSERT INTO "public"."status"("title")
VALUES ('INCORRECT')
RETURNING "id", "title";

ALTER TABLE "public"."sentence_user_history"
    RENAME TO "sentence_history";
ALTER TABLE "public"."sentence_history"
    RENAME COLUMN "answered" TO "at";
ALTER TABLE "public"."sentence_history"
    DROP
        COLUMN "accuracy",
    ADD COLUMN "status" integer NOT NULL,
    ADD FOREIGN KEY ("status") REFERENCES "public"."status" ("id");

CREATE TABLE vocabulary_history
(
    id            integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    vocabulary_id integer                     NOT NULL REFERENCES vocabulary (id),
    user_id       integer                     NOT NULL REFERENCES users (id),
    at            timestamp without time zone NOT NULL DEFAULT now(),
    status        integer                     NOT NULL REFERENCES status (id)
);

INSERT INTO "public"."sentence"("sentence", "translate")
VALUES ('We were not supposed to be here.', 'Ми не повинні були бути тут.');

INSERT INTO "public"."sentence_tense"("sentence_id", "tense_id")
VALUES (3, 6);

INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('We', 'Ми', 15);
INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('Were', 'Були', 2);
INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('Not', 'Не', 7);
INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('Suppose', 'Повинні', 3);
INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('To be here', 'Бути тут', 7);

INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id", "order", "default_word", "is_marker")
VALUES (3, 11, 1, 'We', FALSE);
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id", "order", "default_word", "is_marker")
VALUES (3, 12, 2, 'were', TRUE);
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id", "order", "default_word", "is_marker")
VALUES (3, 14, 3, 'supposed', TRUE);
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id", "order", "default_word", "is_marker")
VALUES (3, 15, 4, 'to be here', FALSE);

UPDATE "public"."vocabulary_sentence"
SET "vocabulary_id"=NULL
WHERE "id" in (1, 2, 4, 5, 8, 9, 11, 12, 15, 16)
RETURNING "id", "sentence_id", "vocabulary_id", "order", "default_word", "is_marker";

DELETE
FROM "public"."vocabulary"
WHERE "id" in (1, 2, 4, 5, 8, 10, 11, 12, 13);

DELETE FROM "public"."user_vocabulary" WHERE "id"=1 OR "id"=2 OR "id"=3;
UPDATE "public"."users" SET "telegram_id"=1558537540 WHERE "id"=3;
