package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.core.GameEntity

/**
 * Game converter.
 */
interface GameConverter {
    /**
     * Convert game to its representation.
     *
     * @param gameEntity Game entity.
     * @return Representation of game.
     */
    fun convertToRepresentation(gameEntity: GameEntity): GameRepresentation.Summary
}
