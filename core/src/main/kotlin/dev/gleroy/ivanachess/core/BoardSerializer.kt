package dev.gleroy.ivanachess.core

/**
 * Board serializer.
 */
interface BoardSerializer {
    /**
     * Serialize board.
     *
     * @param board Board.
     * @return Serialized board.
     */
    fun serialize(board: Board): ByteArray
}
