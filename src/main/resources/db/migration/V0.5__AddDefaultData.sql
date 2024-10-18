INSERT INTO "public"."speech_part"("title", "translate", "answers_to") VALUES('Article', 'Артикль', '--') RETURNING "id", "title", "translate", "answers_to";
ALTER TABLE "public"."sentence" RENAME COLUMN "grammarTask" TO "grammar_task";
