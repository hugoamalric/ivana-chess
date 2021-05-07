package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.core.Match

/**
 * Match converter.
 */
interface MatchConverter {
    /**
     * Convert match to its representation.
     *
     * @param match Match.
     * @return Representation of match.
     */
    fun convertToRepresentation(match: Match): GameRepresentation.Complete
}
