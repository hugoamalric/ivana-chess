package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.game.Position

/**
 * Position converter.
 */
interface PositionConverter : ItemConverter<Position, PositionRepresentation> {
    /**
     * Convert representation to position.
     *
     * @param posRepresentation Representation of position.
     * @return Position.
     */
    fun convertToPosition(posRepresentation: PositionRepresentation): Position
}
