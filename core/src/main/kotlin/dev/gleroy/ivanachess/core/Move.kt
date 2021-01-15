package dev.gleroy.ivanachess.core

/**
 * Move.
 *
 * @param from Start position.
 * @param to Target position.
 */
data class Move(
    val from: Position,
    val to: Position
) {
    companion object {
        /**
         * Instantiate move from coordinates string.
         *
         * @param from Start position coordinates string.
         * @param to Target position coordinates string.
         * @return Move.
         * @throws IllegalArgumentException If coordinates string is invalid.
         */
        @Throws(IllegalArgumentException::class)
        fun fromCoordinates(from: String, to: String) =
            Move(Position.fromCoordinates(from), Position.fromCoordinates(to))
    }
}
