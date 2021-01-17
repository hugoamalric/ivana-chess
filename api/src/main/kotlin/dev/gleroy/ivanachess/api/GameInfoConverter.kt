package dev.gleroy.ivanachess.api

/**
 * Game information converter.
 */
interface GameInfoConverter {
    /**
     * Convert game information to DTO.
     *
     * @param gameInfo Game information.
     * @return Game DTO.
     */
    fun convert(gameInfo: GameInfo): GameDto
}
