package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.game.Position

/**
 * Position converter.
 */
interface PositionConverter {
    /**
     * Convert representation to position.
     *
     * @param posRepresentation Representation of position.
     * @return Position.
     */
    fun convertToPosition(posRepresentation: PositionRepresentation): Position

    /**
     * Convert position to its representation.
     *
     * @param pos Position.
     * @return Representation of position.
     */
    fun convertToRepresentation(pos: Position): PositionRepresentation
}
