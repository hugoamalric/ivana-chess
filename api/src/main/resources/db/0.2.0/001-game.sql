CREATE TABLE game (
    id uuid NOT NULL PRIMARY KEY,
    white_token uuid NOT NULL UNIQUE,
    black_token uuid NOT NULL UNIQUE,
    board varchar(64) NOT NULL
);

CREATE TABLE move (
    game_id uuid NOT NULL REFERENCES game (id) ON DELETE CASCADE,
    "order" smallint NOT NULL,
    "from" varchar(2) NOT NULL,
    "to" varchar(2) NOT NULL
);
