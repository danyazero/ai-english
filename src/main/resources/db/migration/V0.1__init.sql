CREATE TABLE "public"."film"
(
    "id"    integer GENERATED ALWAYS AS IDENTITY,
    "title" text,
    "year"  integer,
    PRIMARY KEY ("id")
);
CREATE TABLE "public"."users"
(
    "id"       integer GENERATED ALWAYS AS IDENTITY,
    "username" text,
    PRIMARY KEY ("id")
);
CREATE TABLE "public"."user_film"
(
    "id"      integer GENERATED ALWAYS AS IDENTITY,
    "film_id" integer,
    "user_id" integer,
    PRIMARY KEY ("id"),
    FOREIGN KEY ("film_id") REFERENCES "public"."film" ("id"),
    FOREIGN KEY ("user_id") REFERENCES "public"."users" ("id")
);

INSERT INTO "public"."film"("title", "year") VALUES('Dune', 2021) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('Money Heist', 2021) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('The Boys', 2019) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('Stranger Things', 2016) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('House M.D.', 2004) RETURNING "id", "title", "year";

INSERT INTO "public"."users"("username") VALUES('danyazero') RETURNING "id", "username";

INSERT INTO "public"."user_film"("film_id", "user_id") VALUES(2, 1) RETURNING "id", "film_id", "user_id";
INSERT INTO "public"."user_film"("film_id", "user_id") VALUES(4, 1) RETURNING "id", "film_id", "user_id";