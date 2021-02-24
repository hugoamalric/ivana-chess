package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.Game

/**
 * Game entity.
 *
 * @param summary Game summary.
 * @param game Game.
 */
data class GameEntity(
    val summary: GameSummary = GameSummary(),
    val game: Game = Game()
)
