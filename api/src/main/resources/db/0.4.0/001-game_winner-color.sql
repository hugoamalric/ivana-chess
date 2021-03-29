ALTER TABLE game
    ADD COLUMN winner_color color DEFAULT NULL;

-- @formatter:off
UPDATE game
SET winner_color = (
    CASE
        WHEN g.turn_color = 'white' THEN 'black'
        ELSE 'white'
    END
)::color
FROM (
         SELECT id, turn_color
         FROM game
         WHERE state = 'checkmate'
     )
    AS g
WHERE game.id = g.id;
-- @formatter:on
