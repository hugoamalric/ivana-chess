package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.game.Move

/**
 * Move converter.
 */
interface MoveConverter : ItemConverter<Move, MoveRepresentation> {
    /**
     * Convert representation to move.
     *
     * @param representation Move representation.
     * @return Move.
     */
    fun convertToMove(representation: MoveRepresentation): Move
}
