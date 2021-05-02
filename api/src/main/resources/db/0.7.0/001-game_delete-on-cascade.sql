ALTER TABLE game
    DROP CONSTRAINT game_white_player_fkey;
ALTER TABLE game
    DROP CONSTRAINT game_black_player_fkey;

ALTER TABLE game
    ADD FOREIGN KEY (white_player) REFERENCES "user" ON DELETE CASCADE;
ALTER TABLE game
    ADD FOREIGN KEY (black_player) REFERENCES "user" ON DELETE CASCADE;
