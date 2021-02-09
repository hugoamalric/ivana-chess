package dev.gleroy.ivanachess.core

/**
 * Move.
 */
sealed class Move {
    /**
     * Promotion move.
     *
     * @param from Start position.
     * @param to Target position.
     * @param promotion Promotion.
     */
    data class Promotion(
        override val from: Position,
        override val to: Position,
        val promotion: Piece
    ) : Move()

    /**
     * Simple move.
     *
     * @param from Start position.
     * @param to Target position.
     */
    data class Simple(
        override val from: Position,
        override val to: Position
    ) : Move() {
        companion object {
            /**
             * Instantiate simple move from coordinates string.
             *
             * @param from Start position coordinates string.
             * @param to Target position coordinates string.
             * @return Simple move.
             * @throws IllegalArgumentException If coordinates string is invalid.
             */
            @Throws(IllegalArgumentException::class)
            fun fromCoordinates(from: String, to: String) = Simple(
                from = Position.fromCoordinates(from),
                to = Position.fromCoordinates(to)
            )
        }

        override fun toString() = "$from$to"
    }

    /**
     * Start position.
     */
    abstract val from: Position

    /**
     * Target position.
     */
    abstract val to: Position
}
