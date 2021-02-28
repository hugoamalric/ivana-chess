package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.core.Game

/**
 * Game and summary.
 *
 * @param summary Game summary.
 * @param game Game.
 */
data class GameAndSummary(
    val summary: GameSummary = GameSummary(),
    val game: Game = Game()
)
