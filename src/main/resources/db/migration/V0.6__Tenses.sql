CREATE TABLE "public"."time"
(
    "id"    integer GENERATED ALWAYS AS IDENTITY,
    "title" varchar(16) NOT NULL,
    PRIMARY KEY ("id")
);

INSERT INTO "public"."time"("title")
VALUES ('Present')
RETURNING "id", "title";
INSERT INTO "public"."time"("title")
VALUES ('Past')
RETURNING "id", "title";
INSERT INTO "public"."time"("title")
VALUES ('Future')
RETURNING "id", "title";


CREATE TABLE "public"."duration"
(
    "id"    integer GENERATED ALWAYS AS IDENTITY,
    "title" text NOT NULL,
    PRIMARY KEY ("id")
);

INSERT INTO "public"."duration"("title")
VALUES ('Simple')
RETURNING "id", "title";
INSERT INTO "public"."duration"("title")
VALUES ('Continuous')
RETURNING "id", "title";
INSERT INTO "public"."duration"("title")
VALUES ('Perfect')
RETURNING "id", "title";
INSERT INTO "public"."duration"("title")
VALUES ('Perfect Continuous')
RETURNING "id", "title";

CREATE TABLE tense
(
    id             integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title_time     integer NOT NULL REFERENCES time (id),
    title_duration integer NOT NULL REFERENCES duration (id),
    formula        text    NOT NULL,
    verb           text    NOT NULL
);

INSERT INTO "public"."tense"("title_time", "title_duration", "formula", "verb")
VALUES (1, 1, 'V (+s)', 'do/does')
RETURNING "id", "title_time", "title_duration", "formula", "verb";
INSERT INTO "public"."tense"("title_time", "title_duration", "formula", "verb")
VALUES (1, 2, 'am/is/are + Ving', 'be')
RETURNING "id", "title_time", "title_duration", "formula", "verb";
INSERT INTO "public"."tense"("title_time", "title_duration", "formula", "verb")
VALUES (1, 3, 'have/has + V3', 'have/has')
RETURNING "id", "title_time", "title_duration", "formula", "verb";
INSERT INTO "public"."tense"("title_time", "title_duration", "formula", "verb")
VALUES (1, 4, 'have/has been + Ving', 'have/has been')
RETURNING "id", "title_time", "title_duration", "formula", "verb";
INSERT INTO "public"."tense"("title_time", "title_duration", "formula", "verb")
VALUES (2, 1, 'V2', 'did')
RETURNING "id", "title_time", "title_duration", "formula", "verb";
INSERT INTO "public"."tense"("title_time", "title_duration", "formula", "verb")
VALUES (2, 2, 'was/were + Ving', 'be')
RETURNING "id", "title_time", "title_duration", "formula", "verb";
INSERT INTO "public"."tense"("title_time", "title_duration", "formula", "verb")
VALUES (2, 3, 'had + V3', 'had')
RETURNING "id", "title_time", "title_duration", "formula", "verb";
INSERT INTO "public"."tense"("title_time", "title_duration", "formula", "verb")
VALUES (2, 4, 'had been + Ving', 'had been')
RETURNING "id", "title_time", "title_duration", "formula", "verb";
INSERT INTO "public"."tense"("title_time", "title_duration", "formula", "verb")
VALUES (3, 1, 'will + V', 'will')
RETURNING "id", "title_time", "title_duration", "formula", "verb";
INSERT INTO "public"."tense"("title_time", "title_duration", "formula", "verb")
VALUES (3, 2, 'will be + Ving', 'will be')
RETURNING "id", "title_time", "title_duration", "formula", "verb";
INSERT INTO "public"."tense"("title_time", "title_duration", "formula", "verb")
VALUES (3, 3, 'will have + V3', 'will have')
RETURNING "id", "title_time", "title_duration", "formula", "verb";
INSERT INTO "public"."tense"("title_time", "title_duration", "formula", "verb")
VALUES (3, 4, 'will have been + Ving', 'will have been')
RETURNING "id", "title_time", "title_duration", "formula", "verb";


CREATE TABLE "public"."sentence_tense"
(
    "id"          integer GENERATED ALWAYS AS IDENTITY,
    "sentence_id" integer NOT NULL,
    "tense_id"    integer NOT NULL,
    PRIMARY KEY ("id"),
    FOREIGN KEY ("sentence_id") REFERENCES "public"."sentence" ("id"),
    FOREIGN KEY ("tense_id") REFERENCES "public"."tense" ("id")
);

INSERT INTO "public"."sentence_tense"("sentence_id", "tense_id")
VALUES (1, 9)
RETURNING "id", "sentence_id", "tense_id";
INSERT INTO "public"."sentence_tense"("sentence_id", "tense_id")
VALUES (2, 2)
RETURNING "id", "sentence_id", "tense_id";