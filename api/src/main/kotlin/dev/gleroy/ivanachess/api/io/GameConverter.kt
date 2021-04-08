package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.api.game.GameEntity
import dev.gleroy.ivanachess.api.game.Match
import dev.gleroy.ivanachess.io.GameRepresentation

/**
 * Game converter.
 */
interface GameConverter {
    /**
     * Convert game entity to its representation.
     *
     * @param gameEntity Game entity.
     * @return Summary representation of game entity.
     */
    fun convertToSummaryRepresentation(gameEntity: GameEntity): GameRepresentation.Summary

    /**
     * Convert match to its representation.
     *
     * @param match Match.
     * @return Complete representation of match.
     */
    fun convertToCompleteRepresentation(match: Match): GameRepresentation.Complete
}
