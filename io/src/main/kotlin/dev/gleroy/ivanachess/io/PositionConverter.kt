package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.game.Position

/**
 * Position converter.
 */
interface PositionConverter {
    /**
     * Convert position to its representation.
     *
     * @param pos Position.
     * @return Representation of position.
     */
    fun convertToRepresentation(pos: Position): PositionRepresentation

    /**
     * Convert representation to position.
     *
     * @param representation Position representation.
     * @return Position.
     */
    fun convertToPosition(representation: PositionRepresentation): Position
}
