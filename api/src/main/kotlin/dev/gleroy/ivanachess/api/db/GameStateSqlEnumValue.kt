package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.game.Game

/**
 * Game state SQL enumeration value.
 *
 * @param label SQL label.
 * @param state Game state.
 */
internal enum class GameStateSqlEnumValue(
    override val label: String,
    val state: Game.State,
) : SqlEnumValue {
    /**
     * In game.
     */
    InGame("in_game", Game.State.InGame),

    /**
     * Checkmate.
     */
    Checkmate("checkmate", Game.State.Checkmate),

    /**
     * Stalemate.
     */
    Stalemate("stalemate", Game.State.Stalemate);

    companion object {
        /**
         * Get game state SQL enumeration value from game state.
         *
         * @param state Game state.
         * @return Game state SQL enumeration value.
         */
        fun from(state: Game.State) = when (state) {
            Game.State.InGame -> InGame
            Game.State.Checkmate -> Checkmate
            Game.State.Stalemate -> Stalemate
        }
    }
}
