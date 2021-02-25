CREATE TABLE "user"
(
    id              uuid                     NOT NULL PRIMARY KEY,
    pseudo          varchar(50)              NOT NULL UNIQUE,
    creation_date   timestamp WITH TIME ZONE NOT NULL,
    bcrypt_password varchar(72)              NOT NULL
);

INSERT INTO "user"
VALUES
    (
        '26e87784-cc92-4193-aaeb-7f1ab03b46ca',
        'admin',
        NOW(),
        '$2y$12$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS'
    );
