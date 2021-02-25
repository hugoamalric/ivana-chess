package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.Game

/**
 * Game state type.
 *
 * @param sqlValue SQL type value.
 * @param state Game state.
 */
internal enum class GameStateType(
    val sqlValue: String,
    val state: Game.State
) {
    InGame("in_game", Game.State.InGame),
    Checkmate("checkmate", Game.State.Checkmate),
    Stalemate("stalemate", Game.State.Stalemate);

    companion object {
        /**
         * Get game state type from game state.
         *
         * @param state Game state.
         * @return Game state type.
         */
        fun from(state: Game.State) = when (state) {
            Game.State.InGame -> InGame
            Game.State.Checkmate -> Checkmate
            Game.State.Stalemate -> Stalemate
        }

        /**
         * Get game state type from SQL type value.
         *
         * @param sqlValue SQL type value.
         * @return Game state type.
         * @throws IllegalArgumentException If SQL type value is not a valid game state.
         */
        @Throws(IllegalArgumentException::class)
        fun from(sqlValue: String) = values().find { it.sqlValue == sqlValue }
            ?: throw IllegalArgumentException("Unknown game state '$sqlValue'")
    }
}
