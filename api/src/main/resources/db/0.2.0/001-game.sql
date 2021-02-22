CREATE TYPE piece_type AS enum ('pawn', 'knight', 'bishop', 'queen', 'king');

CREATE TABLE game
(
    id            uuid                     NOT NULL PRIMARY KEY,
    creation_date timestamp WITH TIME ZONE NOT NULL,
    white_token   uuid                     NOT NULL UNIQUE,
    black_token   uuid                     NOT NULL UNIQUE
);

CREATE TABLE move
(
    game_id   uuid       NOT NULL REFERENCES game (id) ON DELETE CASCADE,
    "order"   smallint   NOT NULL,
    "from"    varchar(2) NOT NULL,
    "to"      varchar(2) NOT NULL,
    promotion piece_type DEFAULT NULL,
    PRIMARY KEY (game_id, "order")
);
