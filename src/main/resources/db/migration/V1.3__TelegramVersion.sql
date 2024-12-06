ALTER TABLE "public"."users"
DROP
COLUMN "first_name",
  DROP
COLUMN "last_name",
  DROP
COLUMN "email",
  DROP
COLUMN "picture",
  ADD COLUMN "telegram_id" bigint NOT NULL DEFAULT '1';

ALTER TABLE "public"."sentence" DROP COLUMN "theme";
ALTER TABLE "public"."sentence" DROP COLUMN "views";

drop table user_theme;
drop table theme;
drop table category;

ALTER TABLE "public"."user_vocabulary" RENAME COLUMN "word_id" TO "vocabulary_id";
ALTER TABLE "public"."user_vocabulary" DROP COLUMN "known";
ALTER TABLE "public"."sentence_user_history" RENAME COLUMN "last_answered" TO "answered";
ALTER TABLE "public"."tense" RENAME COLUMN "title_time" TO "time_id";
ALTER TABLE "public"."tense" RENAME COLUMN "title_duration" TO "duration_id";
ALTER TABLE "public"."user_vocabulary" DROP COLUMN "last_seen";
ALTER TABLE "public"."users" ADD UNIQUE ("telegram_id");
