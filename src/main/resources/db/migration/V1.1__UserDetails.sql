ALTER TABLE "public"."users"
    ADD COLUMN "first_name" text;
ALTER TABLE "public"."users"
    ADD COLUMN "last_name" text;
ALTER TABLE "public"."users"
    ADD COLUMN "email" text;
ALTER TABLE "public"."users"
    ADD COLUMN "picture" text;
ALTER TABLE "public"."users"
    ADD COLUMN "role" varchar(20) NOT NULL DEFAULT 'USER';

UPDATE "public"."users"
SET "first_name"='Данііл',
    "last_name"='Мозжухін',
    "email"='formulamgo2@gmail.com',
    "picture"='https://lh3.googleusercontent.com/a/ACg8ocKmJcgDN2R2D0ivGZJM3lU-WGZx3J8txlm_v6M-SKINp_cZJKal=s96-c'
WHERE "id" = 3
RETURNING "id", "first_name", "last_name", "email", "picture";


delete
from user_theme
where user_id in (1, 2, 4);

delete
from users
where id in (1, 2, 4);

ALTER TABLE "public"."users"
    ALTER COLUMN "first_name" SET NOT NULL,
    ALTER COLUMN "last_name" SET NOT NULL,
    ALTER COLUMN "email" SET NOT NULL,
    ALTER COLUMN "picture" SET NOT NULL;
