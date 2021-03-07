-- noinspection SqlWithoutWhere
DELETE
FROM game;

ALTER TABLE game
    DROP CONSTRAINT game_white_token_key;

ALTER TABLE game
    DROP CONSTRAINT game_black_token_key;

ALTER TABLE game
    RENAME white_token TO white_player;

ALTER TABLE game
    RENAME black_token TO black_player;

ALTER TABLE game
    ADD CONSTRAINT game_white_player_fkey FOREIGN KEY (white_player) REFERENCES "user";

ALTER TABLE game
    ADD CONSTRAINT game_black_player_fkey FOREIGN KEY (black_player) REFERENCES "user";
