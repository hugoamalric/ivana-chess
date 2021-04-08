package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.game.Move

/**
 * Move converter.
 */
interface MoveConverter {
    /**
     * Convert move to its representation.
     *
     * @param move Move.
     * @return Representation of move.
     */
    fun convertToRepresentation(move: Move): MoveRepresentation

    /**
     * Convert representation to move.
     *
     * @param representation Move representation.
     * @return Move.
     */
    fun convertToMove(representation: MoveRepresentation): Move
}
