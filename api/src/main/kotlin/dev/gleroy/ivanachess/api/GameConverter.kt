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
     * @param gameAndSummary Game and summary.
     * @return Game DTO.
     */
    fun convert(gameAndSummary: GameAndSummary): GameDto.Complete
}
