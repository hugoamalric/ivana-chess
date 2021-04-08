package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.core.GameEntity
import dev.gleroy.ivanachess.core.Match

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
