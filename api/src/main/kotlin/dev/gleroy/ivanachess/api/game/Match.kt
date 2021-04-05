package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.core.Game

/**
 * Match.
 *
 * @param entity Game entity.
 * @param game Game.
 */
data class Match(
    val entity: GameEntity,
    val game: Game = Game()
)
