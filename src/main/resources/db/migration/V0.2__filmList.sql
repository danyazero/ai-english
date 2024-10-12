INSERT INTO "public"."film"("title", "year") VALUES('Breaking Bad', 2008) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('Peaky Blinders', 2013) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('Agents of S.H.I.E.L.D.', 2013) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('Supernatural', 2005) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('Riverdale', 2017) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('Teen Wolf', 2011) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('The Vampire Diaries', 2009) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('Lucifer', 2016) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('The Witcher', 2019) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('Wednesday', 2022) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('Squid Game', 2021) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('The 100', 2014) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('The Rookie', 2018) RETURNING "id", "title", "year";
INSERT INTO "public"."film"("title", "year") VALUES('9-1-1', 2018) RETURNING "id", "title", "year";

INSERT INTO "public"."users"("username") VALUES('jakooo') RETURNING "id", "username";
INSERT INTO "public"."users"("username") VALUES('shibasto') RETURNING "id", "username";
INSERT INTO "public"."users"("username") VALUES('delci') RETURNING "id", "username";

INSERT INTO "public"."user_film"("film_id", "user_id") VALUES(2, 2) RETURNING "id", "film_id", "user_id";
INSERT INTO "public"."user_film"("film_id", "user_id") VALUES(2, 3) RETURNING "id", "film_id", "user_id";
INSERT INTO "public"."user_film"("film_id", "user_id") VALUES(3, 3) RETURNING "id", "film_id", "user_id";
INSERT INTO "public"."user_film"("film_id", "user_id") VALUES(11, 1) RETURNING "id", "film_id", "user_id";
INSERT INTO "public"."user_film"("film_id", "user_id") VALUES(16, 4) RETURNING "id", "film_id", "user_id";
INSERT INTO "public"."user_film"("film_id", "user_id") VALUES(8, 3) RETURNING "id", "film_id", "user_id";
INSERT INTO "public"."user_film"("film_id", "user_id") VALUES(4, 2) RETURNING "id", "film_id", "user_id";
INSERT INTO "public"."user_film"("film_id", "user_id") VALUES(9, 4) RETURNING "id", "film_id", "user_id";
INSERT INTO "public"."user_film"("film_id", "user_id") VALUES(9, 1) RETURNING "id", "film_id", "user_id";
INSERT INTO "public"."user_film"("film_id", "user_id") VALUES(9, 2) RETURNING "id", "film_id", "user_id";
