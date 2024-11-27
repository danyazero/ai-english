ALTER TABLE "public"."vocabulary"
    DROP COLUMN "views";

CREATE TABLE user_vocabulary
(
    id        integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id   integer                     NOT NULL REFERENCES users (id),
    word_id   integer                     NOT NULL REFERENCES vocabulary (id),
    known     boolean                     NOT NULL DEFAULT false,
    last_seen timestamp without time zone NOT NULL DEFAULT now()
);
INSERT INTO "public"."user_vocabulary"("user_id", "word_id")
VALUES (3, 6)
RETURNING "id", "user_id", "word_id", "known", "last_seen";
INSERT INTO "public"."user_vocabulary"("user_id", "word_id")
VALUES (3, 9)
RETURNING "id", "user_id", "word_id", "known", "last_seen";
INSERT INTO "public"."user_vocabulary"("user_id", "word_id")
VALUES (3, 3)
RETURNING "id", "user_id", "word_id", "known", "last_seen";
