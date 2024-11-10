UPDATE "public"."user_theme"
SET "theme_id"=6
WHERE "id" = 5
RETURNING "id", "theme_id", "user_id";

CREATE TABLE sentence_user_history
(
    id            integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sentence_id   integer                     NOT NULL REFERENCES sentence (id),
    last_answered timestamp without time zone NOT NULL DEFAULT now(),
    accuracy      double precision            NOT NULL,
    user_id       integer REFERENCES users (id)
);

INSERT INTO "public"."user_theme"("theme_id", "user_id") VALUES(7, 3) RETURNING "id", "theme_id", "user_id";
INSERT INTO "public"."user_theme"("theme_id", "user_id") VALUES(9, 3) RETURNING "id", "theme_id", "user_id";
INSERT INTO "public"."user_theme"("theme_id", "user_id") VALUES(11, 3) RETURNING "id", "theme_id", "user_id";
