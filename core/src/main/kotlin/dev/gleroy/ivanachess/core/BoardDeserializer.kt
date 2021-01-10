package dev.gleroy.ivanachess.core

/**
 * Board deserializer.
 */
interface BoardDeserializer {
    /**
     * Deserialize board.
     *
     * @param bytes Bytes.
     * @return Board.
     * @throws IllegalArgumentException If bytes are not serialized board.
     */
    @Throws(IllegalArgumentException::class)
    fun deserialize(bytes: ByteArray): Board
}
