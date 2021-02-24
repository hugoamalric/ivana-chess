package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.dto.GameDto

/**
 * Game converter.
 */
interface GameConverter {
    /**
     * Convert game summary to DTO.
     *
     * @param gameSummary Game summary.
     * @return Game summary DTO.
     */
    fun convert(gameSummary: GameSummary): GameDto.Summary

    /**
     * Convert entity to DTO.
     *
     * @param gameEntity Game entity.
     * @return Game DTO.
     */
    fun convert(gameEntity: GameEntity): GameDto.Complete
}
