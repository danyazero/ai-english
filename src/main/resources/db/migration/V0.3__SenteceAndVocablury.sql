ALTER TABLE "public"."film"
    RENAME TO "theme";
ALTER TABLE "public"."user_film"
    RENAME TO "user_theme";
ALTER TABLE "public"."user_theme"
    RENAME COLUMN "film_id" TO "theme_id";

CREATE TABLE sentence
(
    id            integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sentence      text    NOT NULL,
    views         integer NOT NULL DEFAULT 0,
    translate     text    NOT NULL,
    "grammarTask" text    NOT NULL,
    theme         integer NOT NULL REFERENCES theme (id)
);


CREATE TABLE speech_part
(
    id         integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title      text NOT NULL UNIQUE,
    translate  text NOT NULL,
    answers_to text NOT NULL
);


CREATE TABLE vocabulary
(
    id             integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    word           text    NOT NULL,
    translate      text    NOT NULL,
    views          integer NOT NULL DEFAULT 0,
    speech_part_id integer NOT NULL REFERENCES speech_part (id)
);

CREATE TABLE vocabulary_sentence
(
    id            integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sentence_id   integer NOT NULL REFERENCES sentence (id),
    vocabulary_id integer NOT NULL REFERENCES vocabulary (id)
);


INSERT INTO "public"."sentence"("sentence", "translate", "grammarTask", "theme")
VALUES ('I will cope with it next Friday', 'Я впораюся з цим наступної пʼятниці', 'I __ cope with it next Friday', 6)
RETURNING "id", "sentence", "views", "translate", "grammarTask", "theme";

INSERT INTO "public"."sentence"("sentence", "translate", "grammarTask", "theme")
VALUES ('I am acing it by next Friday', 'Я зроблю це до наступної п''ятниці', 'I __ acing it by next Friday', 8)
RETURNING "id", "sentence", "views", "translate", "grammarTask", "theme";

INSERT INTO "public"."speech_part"("title", "translate", "answers_to")
VALUES ('Pronoun', 'Займенник', 'Хто? Що?')
RETURNING "id", "title", "translate", "answers_to";
INSERT INTO "public"."speech_part"("title", "translate", "answers_to")
VALUES ('Auxiliary Verb', 'Допоміжне дієслово', '--')
RETURNING "id", "title", "translate", "answers_to";
INSERT INTO "public"."speech_part"("title", "translate", "answers_to")
VALUES ('Verb', 'Дієслово', 'Що робити?')
RETURNING "id", "title", "translate", "answers_to";
INSERT INTO "public"."speech_part"("title", "translate", "answers_to")
VALUES ('Preposition', 'Прийменник', '--')
RETURNING "id", "title", "translate", "answers_to";
INSERT INTO "public"."speech_part"("title", "translate", "answers_to")
VALUES ('Adjective', 'Прикметник', 'Який? Яка? Яке?')
RETURNING "id", "title", "translate", "answers_to";
INSERT INTO "public"."speech_part"("title", "translate", "answers_to")
VALUES ('Noun', 'Іменник', 'Хто? Що?')
RETURNING "id", "title", "translate", "answers_to";
INSERT INTO "public"."speech_part"("title", "translate", "answers_to")
VALUES ('Adverb', 'Прислівник', 'Як? Де?')
RETURNING "id", "title", "translate", "answers_to";
INSERT INTO "public"."speech_part"("title", "translate", "answers_to")
VALUES ('Conjunction', 'Сполучник', '--')
RETURNING "id", "title", "translate", "answers_to";
INSERT INTO "public"."speech_part"("title", "translate", "answers_to")
VALUES ('Interjection', 'Вигук', '--')
RETURNING "id", "title", "translate", "answers_to";

INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('I', 'Я', 1)
RETURNING "id", "word", "translate", "views", "speech_part_id";
INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('Will', 'Буду', 2)
RETURNING "id", "word", "translate", "views", "speech_part_id";
INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('Cope', 'Впоратися', 3)
RETURNING "id", "word", "translate", "views", "speech_part_id";
INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('With', 'З', 4)
RETURNING "id", "word", "translate", "views", "speech_part_id";
INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('It', 'Це', 1)
RETURNING "id", "word", "translate", "views", "speech_part_id";
INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('Next', 'Наступний', 5)
RETURNING "id", "word", "translate", "views", "speech_part_id";
INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('Friday', 'П’ятниця', 6)
RETURNING "id", "word", "translate", "views", "speech_part_id";

INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('Am', 'Бути', 2)
RETURNING "id", "word", "translate", "views", "speech_part_id";
INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('Ace', 'Впоратися на відмінно', 3)
RETURNING "id", "word", "translate", "views", "speech_part_id";
INSERT INTO "public"."vocabulary"("word", "translate", "speech_part_id")
VALUES ('By', 'До', 4)
RETURNING "id", "word", "translate", "views", "speech_part_id";

INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id")
VALUES (1, 1)
RETURNING "id", "sentence_id", "vocabulary_id";
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id")
VALUES (1, 2)
RETURNING "id", "sentence_id", "vocabulary_id";
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id")
VALUES (1, 3)
RETURNING "id", "sentence_id", "vocabulary_id";
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id")
VALUES (1, 4)
RETURNING "id", "sentence_id", "vocabulary_id";
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id")
VALUES (1, 5)
RETURNING "id", "sentence_id", "vocabulary_id";
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id")
VALUES (1, 6)
RETURNING "id", "sentence_id", "vocabulary_id";
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id")
VALUES (1, 7)
RETURNING "id", "sentence_id", "vocabulary_id";
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id")
VALUES (2, 1)
RETURNING "id", "sentence_id", "vocabulary_id";
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id")
VALUES (2, 8)
RETURNING "id", "sentence_id", "vocabulary_id";
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id")
VALUES (2, 9)
RETURNING "id", "sentence_id", "vocabulary_id";
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id")
VALUES (2, 5)
RETURNING "id", "sentence_id", "vocabulary_id";
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id")
VALUES (2, 10)
RETURNING "id", "sentence_id", "vocabulary_id";
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id")
VALUES (2, 6)
RETURNING "id", "sentence_id", "vocabulary_id";
INSERT INTO "public"."vocabulary_sentence"("sentence_id", "vocabulary_id")
VALUES (2, 7)
RETURNING "id", "sentence_id", "vocabulary_id";
